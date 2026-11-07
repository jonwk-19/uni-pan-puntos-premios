package com.umg.edu.mvcweb.dao;

import com.umg.edu.mvcweb.modelo.Cliente;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ClienteDAO {

    List<Cliente> listar() throws SQLException;

    List<Cliente> buscarPorNombre(String texto) throws SQLException;

    Cliente buscarPorId(int id) throws SQLException;

    Cliente buscarPorDocumento(String documento) throws SQLException;

    void insertar(Cliente cliente) throws SQLException;

    void actualizar(Cliente cliente) throws SQLException;

    void cambiarEstado(int id, boolean activo) throws SQLException;

    boolean tieneCompras(int id) throws SQLException;

    void sumarPuntos(Connection con, int idCliente, int puntos) throws SQLException;

    boolean restarPuntos(Connection con, int idCliente, int puntos) throws SQLException;
}
