package com.panpuntos.dao;

import com.panpuntos.modelo.MovimientoPuntos;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface PuntosDAO {

    void registrarMovimiento(Connection con, MovimientoPuntos movimiento) throws SQLException;

    List<MovimientoPuntos> historialPorCliente(int idCliente) throws SQLException;
}
