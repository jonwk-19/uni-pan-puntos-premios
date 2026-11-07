package com.umg.edu.mvcweb.dao;

import com.umg.edu.mvcweb.modelo.Categoria;
import com.umg.edu.mvcweb.modelo.Producto;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ProductoDAO {

    List<Producto> listar() throws SQLException;

    List<Producto> listarPorCategoria(Categoria categoria) throws SQLException;

    List<Producto> listarBajoStock(int limite) throws SQLException;

    Producto buscarPorId(int id) throws SQLException;

    Producto buscarPorCodigo(String codigo) throws SQLException;

    void insertar(Producto producto) throws SQLException;

    void actualizar(Producto producto) throws SQLException;

    void cambiarEstado(int id, boolean activo) throws SQLException;

    boolean descontarExistencia(Connection con, int idProducto, int cantidad) throws SQLException;

    void aumentarExistencia(Connection con, int idProducto, int cantidad) throws SQLException;
}
