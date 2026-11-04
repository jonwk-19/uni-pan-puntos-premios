
package com.panpuntos.servicio;

import com.panpuntos.dao.PedidoDAO;
import com.panpuntos.dao.impl.PedidoDAOImpl;
import com.panpuntos.modelo.EstadoPedido;
import com.panpuntos.modelo.Pedido;
import com.panpuntos.util.ConexionDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PedidoService {

    private final PedidoDAO pedidoDAO = new PedidoDAOImpl();

    public int registrar(Pedido pedido) throws SQLException {

        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser nulo.");
        }

        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new IllegalArgumentException(
                    "El pedido debe tener al menos un producto o menú.");
        }

        double total = pedido.calcularTotal();

        if (total <= 0) {
            throw new IllegalArgumentException(
                    "El total del pedido debe ser mayor que cero.");
        }

        try (Connection con = ConexionDB.getConexion()) {
            con.setAutoCommit(false);

            try {
                pedido.setTotal(total);
                pedido.setEstado(EstadoPedido.PENDIENTE);
                pedido.setPuntosGanados(0);

                int idPedido = pedidoDAO.crear(con, pedido);

                con.commit();
                return idPedido;

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public List<Pedido> listar() throws SQLException {
        return pedidoDAO.listar();
    }

    public List<Pedido> listarPorCliente(int idCliente)
            throws SQLException {

        if (idCliente <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del cliente no es válido.");
        }

        return pedidoDAO.listarPorCliente(idCliente);
    }

    public Pedido buscarPorId(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del pedido no es válido.");
        }

        return pedidoDAO.buscarPorId(id);
    }

    public void entregar(int idPedido) throws SQLException {

        cambiarEstado(idPedido, EstadoPedido.PAGADO,
                EstadoPedido.ENTREGADO);
    }

    public void anular(int idPedido) throws SQLException {

        if (idPedido <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del pedido no es válido.");
        }

        try (Connection con = ConexionDB.getConexion()) {
            con.setAutoCommit(false);

            try {
                Pedido pedido = bloquearPedido(con, idPedido);

                if (pedido == null) {
                    throw new IllegalArgumentException(
                            "El pedido no existe.");
                }

                if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
                    throw new IllegalArgumentException(
                            "Solo se pueden anular pedidos pendientes.");
                }

                pedidoDAO.actualizarEstado(
                        con, idPedido, EstadoPedido.ANULADO);

                con.commit();

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    private void cambiarEstado(int idPedido,
            EstadoPedido estadoActual,
            EstadoPedido estadoNuevo) throws SQLException {

        if (idPedido <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del pedido no es válido.");
        }

        try (Connection con = ConexionDB.getConexion()) {
            con.setAutoCommit(false);

            try {
                Pedido pedido = bloquearPedido(con, idPedido);

                if (pedido == null) {
                    throw new IllegalArgumentException(
                            "El pedido no existe.");
                }

                if (pedido.getEstado() != estadoActual) {
                    throw new IllegalArgumentException(
                            "El pedido no tiene el estado necesario para esta operación.");
                }

                pedidoDAO.actualizarEstado(
                        con, idPedido, estadoNuevo);

                con.commit();

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    private Pedido bloquearPedido(Connection con, int idPedido)
            throws SQLException {

        String sql = "SELECT id_pedido FROM pedidos "
                + "WHERE id_pedido = ? FOR UPDATE";

        try (java.sql.PreparedStatement ps =
                con.prepareStatement(sql)) {

            ps.setInt(1, idPedido);

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return pedidoDAO.buscarPorId(idPedido);
                }
            }
        }

        return null;
    }
}
