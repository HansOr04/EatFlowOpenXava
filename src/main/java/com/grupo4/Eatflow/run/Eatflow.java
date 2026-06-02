package com.grupo4.Eatflow.run;

import org.openxava.util.*;

/**
 * Ejecuta esta clase para arrancar la aplicación.
 */

public class Eatflow {

	public static void main(String[] args) throws Exception {
		DBServer.start("Eatflow-db"); // Para usar tu propia base de datos comenta esta línea y configura src/main/webapp/META-INF/context.xml
		AppServer.run("Eatflow"); // Usa AppServer.run("") para funcionar en el contexto raíz
	}

}
