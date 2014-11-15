package utn.algo2.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;

import utn.algo2.annotations.Control;
import utn.algo2.annotations.Label;
import utn.algo2.annotations.NotNull;
import utn.algo2.annotations.Personalizada;
import utn.algo2.annotations.ValidacionPersonalizada;
import utn.algo2.exception.TipoInvalidoException;
import utn.algo2.validaciones.Validacion2;

public class Atributo<T> {

	private Field field;
	private Object valor;

	public Atributo(Field aKey) {
		this.field = aKey;
		field.setAccessible(true);
	}

	public Atributo(Field aKey, Object aValue) {
		this.field = aKey;
		this.valor = aValue;
		field.setAccessible(true);
	}

	/* Metodos */

	public void setIn(T destino) throws TipoInvalidoException {
		
		Object valorField = castearValor();

		evaluarAnotaciones(destino, valorField);

		settearValor(destino, valorField);
	}

	

	public void getValorFrom(T fuente) {
		try {
			valor = field.get(fuente).toString();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/* Complementarios */
	
	private Object castearValor() {
		Constructor<?> constructor = null;
		
		try {
			constructor = field.getType().getConstructor(String.class);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		}

		Object valorField = null;
		
		try {
			valorField = constructor.newInstance(valor);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			throw new TipoInvalidoException("Tipo invalido");
		}
		
		return valorField;
	}

	private void evaluarAnotaciones(T destino, Object valorField) {
		for (Annotation a : field.getAnnotations()) {
			String nombre = a.annotationType().getSimpleName();
			
			Method metodo1 = null;
			Object objeto1 = null;
			
			if (nombre.equals("Personalizada")) {
				Personalizada personalizada = (Personalizada) a;
				metodo1 = getMetodo(personalizada.metodo(), destino.getClass(), valorField.getClass());
				objeto1 = destino;

			} else {
				Class<?> clase = null;
				try {
					clase = Class.forName("utn.algo2.validaciones." + nombre);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				try {
					objeto1 = clase.newInstance();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}

				metodo1 = getMetodo("evaluar", clase, Object.class);
			}

			invocar(metodo1, objeto1, valorField);
		}
	}

	private Method getMetodo(String nombre, Class<?> clase, Class<? extends Object> class1) {
		Method metodo = null;
		try {
			metodo = clase.getMethod(nombre, class1);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return metodo;
	}

	private void invocar(Method metodo, Object objeto, Object valorField) {
		try {
			boolean resultado = (boolean) metodo.invoke(objeto, valorField);
			System.out.println("Field: " + field.getName() + " se valida con " + objeto.getClass().getSimpleName() + "." + metodo.getName() + " y da " + resultado);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void settearValor(T destino, Object valorField) {
		try {
			field.set(destino, valorField);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/* Overrides */

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object otro) {
		if (otro instanceof Atributo) {
			Atributo<T> otroAtributo = (Atributo<T>) otro;
			if (this.field.getName().equals(otroAtributo.field.getName())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/* Getters y Setters */

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Object getValor() {
		return valor;
	}

	public void setValor(Object valor) {
		this.valor = valor;
	}

	/*
	 * Pregunto si tiene annotations
	 */
	
	public boolean esNulable() {
		return !this.field.isAnnotationPresent(NotNull.class);
	}

	public boolean esControl() {
		return !this.field.isAnnotationPresent(Control.class);
	}

	public boolean esLabel() {
		return !this.field.isAnnotationPresent(Label.class);
	}

	public boolean tieneValidacion() {
		return !this.field.isAnnotationPresent(ValidacionPersonalizada.class);
	}

	public Validacion2 obtengoValidacion() {
		ValidacionPersonalizada annotation = this.field
				.getAnnotation(ValidacionPersonalizada.class);
		return annotation.metodo();
	}

	public boolean cumpleValidacion() {
		if (this.tieneValidacion()) {
			Validacion2 validacion = this.obtengoValidacion();
			validacion.getValidador().evaluaValidacion(this.valor);
		}
		return true;
	}
}
