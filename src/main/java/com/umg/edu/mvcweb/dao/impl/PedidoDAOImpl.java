
package com.umg.edu.mvcweb.dao.impl;

import com.umg.edu.mvcweb.dao.PedidoDAO;
import com.umg.edu.mvcweb.modelo.DetallePedido;
import com.umg.edu.mvcweb.modelo.EstadoPedido;
import com.umg.edu.mvcweb.modelo.Pedido;
import com.umg.edu.mvcweb.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAOImpl implements PedidoDAO {

    @Override
    public int crear(Connection con, Pedido pedido) throws SQLException {

        String sqlPedido = "INSERT INTO pedidos "
                + "(id_cliente, estado, total, puntos_ganados) "
                + "VALUES (?, ?, ?, ?)";

        String sqlDetalle = "INSERT INTO detalle_pedido "
                + "(id_pedido, id_producto, id_menu, cantidad, precio_unitario) "
                + "VALUES (?, ?, ?, ?, ?)";

        double total = pedido.calcularTotal();

        try (PreparedStatement ps = con.prepareStatement(
                sqlPedido, Statement.RETURN_GENERATED_KEYS)) {

            if (pedido.getIdCliente() == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, pedido.getIdCliente());
            }

            ps.setString(2, EstadoPedido.PENDIENTE.name());
            ps.setDouble(3, total);
            ps.setInt(4, pedido.getPuntosGanados());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("No se pudo crear el pedido.");
                }

                int idPedido = rs.getInt(1);
                pedido.setId(idPedido);
                pedido.setTotal(total);
                pedido.setEstado(EstadoPedido.PENDIENTE);

                try (PreparedStatement psDetalle =
                        con.prepareStatement(sqlDetalle)) {

                    for (DetallePedido detalle : pedido.getDetalles()) {

                        if (detalle.getCantidad() <= 0) {
                            throw new SQLException(
                                    "La cantidad debe ser mayor que cero.");
                        }

                        if ((detalle.getIdProducto() == null)
                                == (detalle.getIdMenu() == null)) {
                            throw new SQLException(
                                    "El detalle debe tener un producto o un menú.");
                        }

                        psDetalle.setInt(1, idPedido);

                        if (detalle.getIdProducto() == null) {
                            psDetalle.setNull(2, Types.INTEGER);
                        } else {
                            psDetalle.setInt(2, detalle.getIdProducto());
                        }

                        if (detalle.getIdMenu() == null) {
                            psDetalle.setNull(3, Types.INTEGER);
                        } else {
                            psDetalle.setInt(3, detalle.getIdMenu());
                        }

                        psDetalle.setInt(4, detalle.getCantidad());
                        psDetalle.setDouble(5, detalle.getPrecioUnitario());
                        psDetalle.executeUpdate();

                        detalle.setIdPedido(idPedido);
                    }
                }

                return idPedido;
            }
        }
    }

    @Override
    public List<Pedido> listar() throws SQLException {

        String sql = "SELECT * FROM pedidos ORDER BY fecha DESC";
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pedido pedido = mapear(rs);
                cargarDetalles(con, pedido);
                pedidos.add(pedido);
            }
        }

        return pedidos;
    }

    @Override
    public List<Pedido> listarPorCliente(int idCliente)
            throws SQLException {

        String sql = "SELECT * FROM pedidos "
                + "WHERE id_cliente = ? ORDER BY fecha DESC";

        List<Pedido> pedidos = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pedido pedido = mapear(rs);
                    cargarDetalles(con, pedido);
                    pedidos.add(pedido);
                }
            }
        }

        return pedidos;
    }

    @Override
    public Pedido buscarPorId(int id) throws SQLException {

        String sql = "SELECT * FROM pedidos WHERE id_pedido = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Pedido pedido = mapear(rs);
                    cargarDetalles(con, pedido);
                    return pedido;
                }
            }
        }

        return null;
    }

    @Override
    public void actualizarEstado(Connection con, int idPedido,
            EstadoPedido estado) throws SQLException {

        String sql = "UPDATE pedidos SET estado = ? WHERE id_pedido = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado.name());
            ps.setInt(2, idPedido);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró el pedido.");
            }
        }
    }

    @Override
    public void actualizarPuntos(Connection con, int idPedido,
            int puntos) throws SQLException {

        String sql = "UPDATE pedidos SET puntos_ganados = ? "
                + "WHERE id_pedido = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, puntos);
            ps.setInt(2, idPedido);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró el pedido.");
            }
        }
    }

    private Pedido mapear(ResultSet rs) throws SQLException {

        Pedido pedido = new Pedido();

        pedido.setId(rs.getInt("id_pedido"));

        int idCliente = rs.getInt("id_cliente");
        pedido.setIdCliente(rs.wasNull() ? null : idCliente);

        Timestamp fecha = rs.getTimestamp("fecha");
        if (fecha != null) {
            pedido.setFecha(fecha.toLocalDateTime());
        }

        pedido.setEstado(
                EstadoPedido.valueOf(rs.getString("estado")));

        pedido.setTotal(rs.getDouble("total"));
        pedido.setPuntosGanados(rs.getInt("puntos_ganados"));

        return pedido;
    }

    private void cargarDetalles(Connection con, Pedido pedido)
            throws SQLException {

        String sql = "SELECT d.*, "
                + "p.nombre AS nombre_producto, "
                + "m.nombre AS nombre_menu "
                + "FROM detalle_pedido d "
                + "LEFT JOIN productos p ON d.id_producto = p.id_producto "
                + "LEFT JOIN menus m ON d.id_menu = m.id_menu "
                + "WHERE d.id_pedido = ? "
                + "ORDER BY d.id_detalle";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pedido.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    DetallePedido detalle = new DetallePedido();

                    detalle.setId(rs.getInt("id_detalle"));
                    detalle.setIdPedido(rs.getInt("id_pedido"));

                    int idProducto = rs.getInt("id_producto");
                    detalle.setIdProducto(
                            rs.wasNull() ? null : idProducto);

                    int idMenu = rs.getInt("id_menu");
                    detalle.setIdMenu(
                            rs.wasNull() ? null : idMenu);

                    String nombre = rs.getString("nombre_producto");
                    if (nombre == null) {
                        nombre = rs.getString("nombre_menu");
                    }

                    detalle.setNombre(nombre);
                    detalle.setCantidad(rs.getInt("cantidad"));
                    detalle.setPrecioUnitario(
                            rs.getDouble("precio_unitario"));

                    pedido.agregarDetalle(detalle);
                }
            }
        }
    }
}
