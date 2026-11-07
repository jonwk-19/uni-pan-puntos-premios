package com.umg.edu.mvcweb.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL = valor("DB_URL",
            "jdbc:mysql://localhost:3306/pan_puntos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
    private static final String USUARIO = valor("DB_USER", "root");
    private static final String CLAVE = valor("DB_PASSWORD", "root");

    private static String valor(String nombre, String porDefecto) {
        String variable = System.getenv(nombre);
        return variable == null || variable.isBlank() ? porDefecto : variable;
    }

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
