package com.umg.edu.mvcweb.dao;

import com.umg.edu.mvcweb.modelo.EstadoPedido;
import com.umg.edu.mvcweb.modelo.Pedido;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface PedidoDAO {

    int crear(Connection con, Pedido pedido) throws SQLException;

    List<Pedido> listar() throws SQLException;

    List<Pedido> listarPorCliente(int idCliente) throws SQLException;

    Pedido buscarPorId(int id) throws SQLException;

    void actualizarEstado(Connection con, int idPedido, EstadoPedido estado) throws SQLException;

    void actualizarPuntos(Connection con, int idPedido, int puntos) throws SQLException;
}
