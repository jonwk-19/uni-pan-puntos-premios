
package com.panpuntos.servicio;

import com.panpuntos.dao.impl.PuntosDAOImpl;
import com.panpuntos.modelo.MovimientoPuntos;
import com.panpuntos.modelo.Recompensa;
import com.panpuntos.modelo.TipoMovimientoPuntos;
import com.panpuntos.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecompensaServicio {

    private final PuntosDAOImpl puntosDAO = new PuntosDAOImpl();

    /**
     * Realiza el canje de una recompensa.
     *
     * @return true si el canje se realizó correctamente.
     */
    public boolean canjear(int idCliente, int idRecompensa)
            throws SQLException {

        if (idCliente <= 0 || idRecompensa <= 0) {
            throw new IllegalArgumentException(
                    "Los identificadores deben ser válidos."
            );
        }

        try (Connection con = ConexionDB.getConexion()) {

            con.setAutoCommit(false);

            try {
                // 1. Bloquear y verificar al cliente.
                String sqlCliente =
                        "SELECT puntos FROM clientes "
                        + "WHERE id_cliente = ? AND activo = TRUE "
                        + "FOR UPDATE";

                int puntosCliente;

                try (PreparedStatement ps = con.prepareStatement(sqlCliente)) {
                    ps.setInt(1, idCliente);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "El cliente no existe o está inactivo."
                            );
                        }

                        puntosCliente = rs.getInt("puntos");
                    }
                }

                // 2. Consultar y bloquear la recompensa.
                Recompensa recompensa =
                        buscarRecompensa(con, idRecompensa);

                if (recompensa == null || !recompensa.isActiva()) {
                    throw new SQLException(
                            "La recompensa no existe o está inactiva."
                    );
                }

                int costo = recompensa.getPuntosRequeridos();

                if (costo <= 0) {
                    throw new SQLException(
                            "El costo de la recompensa no es válido."
                    );
                }

                // 3. Comprobar puntos suficientes.
                if (puntosCliente < costo) {
                    con.rollback();
                    return false;
                }

                // 4. Descontar los puntos.
                String sqlDescuento =
                        "UPDATE clientes SET puntos = puntos - ? "
                        + "WHERE id_cliente = ? "
                        + "AND activo = TRUE AND puntos >= ?";

                try (PreparedStatement ps = con.prepareStatement(sqlDescuento)) {
                    ps.setInt(1, costo);
                    ps.setInt(2, idCliente);
                    ps.setInt(3, costo);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudieron descontar los puntos."
                        );
                    }
                }

                // 5. Registrar el movimiento de canje.
                MovimientoPuntos movimiento = new MovimientoPuntos();

                movimiento.setIdCliente(idCliente);
                movimiento.setPuntos(costo);
                movimiento.setTipo(TipoMovimientoPuntos.CANJE);
                movimiento.setIdPedido(null);
                movimiento.setIdRecompensa(idRecompensa);

                puntosDAO.registrarMovimiento(con, movimiento);

                // 6. Confirmar la transacción.
                con.commit();

                return true;

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    /**
     * Busca una recompensa utilizando la misma conexión
     * de la transacción y bloquea su registro.
     */
    private Recompensa buscarRecompensa(Connection con, int id)
            throws SQLException {

        String sql =
                "SELECT id_recompensa, nombre, puntos_requeridos, "
                + "id_producto, id_menu, activa "
                + "FROM recompensas "
                + "WHERE id_recompensa = ? "
                + "FOR UPDATE";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                Recompensa recompensa = new Recompensa();

                recompensa.setId(rs.getInt("id_recompensa"));
                recompensa.setNombre(rs.getString("nombre"));
                recompensa.setPuntosRequeridos(
                        rs.getInt("puntos_requeridos")
                );

                int idProducto = rs.getInt("id_producto");
                recompensa.setIdProducto(
                        rs.wasNull() ? null : idProducto
                );

                int idMenu = rs.getInt("id_menu");
                recompensa.setIdMenu(
                        rs.wasNull() ? null : idMenu
                );

                recompensa.setActiva(rs.getBoolean("activa"));

                return recompensa;
            }
        }
    }
}

