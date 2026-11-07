package com.umg.edu.mvcweb.dao;

import com.umg.edu.mvcweb.modelo.MovimientoInventario;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface InventarioDAO {

    void registrarMovimiento(Connection con, MovimientoInventario movimiento) throws SQLException;

    List<MovimientoInventario> historial() throws SQLException;

    List<MovimientoInventario> historialPorProducto(int idProducto) throws SQLException;
}
