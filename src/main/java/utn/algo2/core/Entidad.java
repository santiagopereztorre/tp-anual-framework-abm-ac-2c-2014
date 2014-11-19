package utn.algo2.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import utn.algo2.exception.CasteoInvalidoException;
import utn.algo2.exception.ValorNoCumpleCondicionException;

public class Entidad<T> {

	private Class<?> clase;
	private List<Atributo<T>> atributos = new ArrayList<Atributo<T>>();
	private T objeto;

	/* Interfaz */

	public T crearObjeto() throws CasteoInvalidoException,
			ValorNoCumpleCondicionException, RuntimeException {
		if (objeto == null)
			objeto = crearInstancia(clase);
		for (Atributo<T> atributo : atributos) {
			atributo.setIn(objeto);
		}
		return objeto;
	}

	public void actualizarAtributosFrom(T unObjeto) {
		objeto = unObjeto;
		for (Field field : clase.getDeclaredFields()) {
			Atributo<T> atributo = new Atributo<T>(field);
			atributo.getValorFrom(unObjeto);
			atributos.add(atributo);
		}
	}

	/* Reflection */

	@SuppressWarnings("unchecked")
	private T crearInstancia(Class<?> unaClase) {
		T objeto = null;
		try {
			objeto = (T) unaClase.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		return objeto;
	}

	/* Setters y Getters */

	public Class<?> getClase() {
		return clase;
	}

	public void setClase(Class<?> unaClase) {
		clase = unaClase;
	}

	public void setValor(Atributo<T> atributo2, Object unValor) {
		atributo2.setValor(unValor);
		atributos.add(atributo2);
	}

	public Object getValor(Atributo<T> atributo) {
		int index = atributos.indexOf(atributo);
		return atributos.get(index).getValor();
	}

	public boolean isEmpty() {
		return atributos.isEmpty();
	}

}
