package com.umg.edu.mvcweb.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL = valor("DB_URL",
            "jdbc:mysql://localhost:3307/pan_puntos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");

    private static final String USUARIO = valor("DB_USER", "panpuntos_app");

    private static final String CLAVE = System.getenv("DB_PASSWORD");

    private static String valor(String nombre, String porDefecto) {
        String variable = System.getenv(nombre);
        return variable == null || variable.isBlank() ? porDefecto : variable;
    }

    public static Connection getConexion() throws SQLException {
        if (CLAVE == null || CLAVE.isBlank()) {
            throw new SQLException("No se ha configurado DB_PASSWORD.");
        }

        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}