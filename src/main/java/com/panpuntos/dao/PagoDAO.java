package com.panpuntos.dao;

import com.panpuntos.modelo.Pago;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface PagoDAO {

    void registrar(Connection con, Pago pago) throws SQLException;

    List<Pago> listarPorPedido(int idPedido) throws SQLException;
}
