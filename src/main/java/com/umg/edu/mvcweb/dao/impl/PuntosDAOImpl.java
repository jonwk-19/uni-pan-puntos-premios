package com.umg.edu.mvcweb.dao.impl;

import com.umg.edu.mvcweb.dao.PuntosDAO;
import com.umg.edu.mvcweb.modelo.MovimientoPuntos;
import com.umg.edu.mvcweb.modelo.TipoMovimientoPuntos;
import com.umg.edu.mvcweb.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class PuntosDAOImpl implements PuntosDAO {

    @Override
    public void registrarMovimiento(Connection con, MovimientoPuntos movimiento)
            throws SQLException {

        if (con == null) {
            throw new SQLException("La conexión no está disponible.");
        }

        if (movimiento == null || movimiento.getPuntos() == 0
                || movimiento.getTipo() == null) {
            throw new SQLException("Los datos del movimiento no son válidos.");
        }

        String sql = "INSERT INTO movimientos_puntos "
                + "(id_cliente, puntos, tipo, id_pedido, id_recompensa) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, movimiento.getIdCliente());
            ps.setInt(2, movimiento.getPuntos());
            ps.setString(3, movimiento.getTipo().name());

            if (movimiento.getIdPedido() != null) {
                ps.setInt(4, movimiento.getIdPedido());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }

            if (movimiento.getIdRecompensa() != null) {
                ps.setInt(5, movimiento.getIdRecompensa());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            ps.executeUpdate();
        }
    }

    @Override
    public List<MovimientoPuntos> historialPorCliente(int idCliente)
            throws SQLException {

        String sql = "SELECT id_movimiento, id_cliente, fecha, puntos, tipo, "
                + "id_pedido, id_recompensa "
                + "FROM movimientos_puntos "
                + "WHERE id_cliente = ? "
                + "ORDER BY fecha DESC, id_movimiento DESC";

        List<MovimientoPuntos> movimientos = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    MovimientoPuntos movimiento = new MovimientoPuntos();

                    movimiento.setId(rs.getInt("id_movimiento"));
                    movimiento.setIdCliente(rs.getInt("id_cliente"));

                    Timestamp fecha = rs.getTimestamp("fecha");

                    if (fecha != null) {
                        movimiento.setFecha(fecha.toLocalDateTime());
                    }

                    movimiento.setPuntos(rs.getInt("puntos"));

                    movimiento.setTipo(
                            TipoMovimientoPuntos.valueOf(
                                    rs.getString("tipo")
                            )
                    );

                    int idPedido = rs.getInt("id_pedido");
                    movimiento.setIdPedido(
                            rs.wasNull() ? null : idPedido
                    );

                    int idRecompensa = rs.getInt("id_recompensa");
                    movimiento.setIdRecompensa(
                            rs.wasNull() ? null : idRecompensa
                    );

                    movimientos.add(movimiento);
                }
            }
        }

        return movimientos;
    }
}
