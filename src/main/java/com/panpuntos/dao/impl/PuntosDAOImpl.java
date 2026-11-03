
package com.panpuntos.dao.impl;

import com.panpuntos.dao.PuntosDAO;
import com.panpuntos.modelo.MovimientoPuntos;
import com.panpuntos.modelo.TipoMovimientoPuntos;
import com.panpuntos.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PuntosDAOImpl implements PuntosDAO {

    @Override
    public void registrarMovimiento(Connection con, MovimientoPuntos movimiento)
            throws SQLException {

        String sql = "INSERT INTO movimientos_puntos "
                + "(id_cliente, puntos, tipo, id_pedido, id_recompensa) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, movimiento.getIdCliente());
            ps.setInt(2, movimiento.getPuntos());
            ps.setString(3, movimiento.getTipo().name());

            if (movimiento.getIdPedido() == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, movimiento.getIdPedido());
            }

            if (movimiento.getIdRecompensa() == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, movimiento.getIdRecompensa());
            }

            ps.executeUpdate();
        }
    }

    @Override
    public List<MovimientoPuntos> historialPorCliente(int idCliente)
            throws SQLException {

        String sql = "SELECT id_movimiento, id_cliente, fecha, puntos, "
                + "tipo, id_pedido, id_recompensa "
                + "FROM movimientos_puntos "
                + "WHERE id_cliente = ? "
                + "ORDER BY fecha DESC, id_movimiento DESC";

        List<MovimientoPuntos> movimientos = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MovimientoPuntos m = new MovimientoPuntos();

                    m.setId(rs.getInt("id_movimiento"));
                    m.setIdCliente(rs.getInt("id_cliente"));
                    m.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                    m.setPuntos(rs.getInt("puntos"));
                    m.setTipo(TipoMovimientoPuntos.valueOf(
                            rs.getString("tipo")));

                    int idPedido = rs.getInt("id_pedido");
                    m.setIdPedido(rs.wasNull() ? null : idPedido);

                    int idRecompensa = rs.getInt("id_recompensa");
                    m.setIdRecompensa(
                            rs.wasNull() ? null : idRecompensa);

                    movimientos.add(m);
                }
            }
        }

        return movimientos;
    }
}
