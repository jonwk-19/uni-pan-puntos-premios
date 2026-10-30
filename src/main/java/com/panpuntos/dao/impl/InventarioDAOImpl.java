package com.panpuntos.dao.impl;

import com.panpuntos.dao.InventarioDAO;
import com.panpuntos.modelo.MovimientoInventario;
import com.panpuntos.modelo.TipoMovimientoInventario;
import com.panpuntos.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAOImpl implements InventarioDAO {

    private static final String SELECT =
            "SELECT m.id_movimiento, m.id_producto, p.nombre, m.fecha, m.cantidad, m.tipo, m.id_pedido "
            + "FROM movimientos_inventario m JOIN productos p ON p.id_producto = m.id_producto";

    private static final String ORDEN = " ORDER BY m.fecha DESC, m.id_movimiento DESC";

    @Override
    public void registrarMovimiento(Connection con, MovimientoInventario movimiento) throws SQLException {
        String sql = "INSERT INTO movimientos_inventario (id_producto, cantidad, tipo, id_pedido) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, movimiento.getIdProducto());
            ps.setInt(2, movimiento.getCantidad());
            ps.setString(3, movimiento.getTipo().name());
            if (movimiento.getIdPedido() == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, movimiento.getIdPedido());
            }
            ps.executeUpdate();
        }
    }

    @Override
    public List<MovimientoInventario> historial() throws SQLException {
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SELECT + ORDEN)) {
            return leer(ps);
        }
    }

    @Override
    public List<MovimientoInventario> historialPorProducto(int idProducto) throws SQLException {
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(SELECT + " WHERE m.id_producto = ?" + ORDEN)) {
            ps.setInt(1, idProducto);
            return leer(ps);
        }
    }

    private List<MovimientoInventario> leer(PreparedStatement ps) throws SQLException {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MovimientoInventario m = new MovimientoInventario();
                m.setId(rs.getInt("id_movimiento"));
                m.setIdProducto(rs.getInt("id_producto"));
                m.setNombreProducto(rs.getString("nombre"));
                m.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                m.setCantidad(rs.getInt("cantidad"));
                m.setTipo(TipoMovimientoInventario.valueOf(rs.getString("tipo")));
                int idPedido = rs.getInt("id_pedido");
                m.setIdPedido(rs.wasNull() ? null : idPedido);
                movimientos.add(m);
            }
        }
        return movimientos;
    }
}
