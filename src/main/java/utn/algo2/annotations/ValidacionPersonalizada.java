package utn.algo2.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Al validar el campo, se corre el metodo "metodo" de la clase del campo.
 * El metodo debe ser de tipo Static con la firma "boolean metodo(T unElemento)"
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidacionPersonalizada {
	String metodo();
}
