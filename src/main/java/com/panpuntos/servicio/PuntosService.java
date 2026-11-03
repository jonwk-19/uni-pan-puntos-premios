

package com.panpuntos.servicio;

import com.panpuntos.dao.impl.PuntosDAOImpl;
import com.panpuntos.modelo.EstadoPago;
import com.panpuntos.modelo.Menu;
import com.panpuntos.modelo.MovimientoPuntos;
import com.panpuntos.modelo.Sandwich;
import com.panpuntos.modelo.TipoMovimientoPuntos;
import com.panpuntos.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PuntosService {

    private final PuntosDAOImpl puntosDAO = new PuntosDAOImpl();

    /**
     * Acredita los puntos de un pedido pagado y con pago aprobado.
     *
     * @return cantidad de puntos acreditados.
     * Retorna 0 si ya fue procesado o no genera puntos.
     */
    public int acreditar(int idPedido) throws SQLException {

        if (idPedido <= 0) {
            throw new IllegalArgumentException(
                    "El ID del pedido no es válido."
            );
        }

        try (Connection con = ConexionDB.getConexion()) {

            con.setAutoCommit(false);

            try {
                int idCliente;
                int puntosGanados;
                String estadoPedido;

                // 1. Bloquear y consultar el pedido.
                String sqlPedido =
                        "SELECT id_cliente, puntos_ganados, estado "
                        + "FROM pedidos WHERE id_pedido = ? FOR UPDATE";

                try (PreparedStatement ps = con.prepareStatement(sqlPedido)) {
                    ps.setInt(1, idPedido);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("El pedido no existe.");
                        }

                        idCliente = rs.getInt("id_cliente");

                        if (rs.wasNull()) {
                            con.rollback();
                            return 0;
                        }

                        puntosGanados = rs.getInt("puntos_ganados");
                        estadoPedido = rs.getString("estado");
                    }
                }

                // 2. Verificar que el pedido esté PAGADO.
                if (!"PAGADO".equals(estadoPedido)) {
                    con.rollback();
                    return 0;
                }

                // 3. Comprobar que exista un pago aprobado.
                String sqlPago =
                        "SELECT COUNT(*) FROM pagos "
                        + "WHERE id_pedido = ? AND estado = ?";

                try (PreparedStatement ps = con.prepareStatement(sqlPago)) {
                    ps.setInt(1, idPedido);
                    ps.setString(2, EstadoPago.APROBADO.name());

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();

                        if (rs.getInt(1) == 0) {
                            con.rollback();
                            return 0;
                        }
                    }
                }

                // 4. Evitar acreditar dos veces el mismo pedido.
                String sqlDuplicado =
                        "SELECT COUNT(*) FROM movimientos_puntos "
                        + "WHERE id_pedido = ? "
                        + "AND tipo = 'ACUMULACION'";

                try (PreparedStatement ps = con.prepareStatement(sqlDuplicado)) {
                    ps.setInt(1, idPedido);

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();

                        if (rs.getInt(1) > 0 || puntosGanados > 0) {
                            con.rollback();
                            return 0;
                        }
                    }
                }

                // 5. Calcular puntos según los detalles del pedido.
                int puntosSandwich = new Sandwich().getPuntos();
                int puntosMenu = new Menu().getPuntos();

                String sqlCalculo =
                        "SELECT COALESCE(SUM("
                        + "CASE "
                        + "WHEN dp.id_menu IS NOT NULL "
                        + "THEN dp.cantidad * ? "
                        + "WHEN p.categoria = 'SANDWICH' "
                        + "THEN dp.cantidad * ? "
                        + "ELSE 0 END), 0) AS puntos "
                        + "FROM detalle_pedido dp "
                        + "LEFT JOIN productos p "
                        + "ON dp.id_producto = p.id_producto "
                        + "WHERE dp.id_pedido = ?";

                int puntos;

                try (PreparedStatement ps = con.prepareStatement(sqlCalculo)) {
                    ps.setInt(1, puntosMenu);
                    ps.setInt(2, puntosSandwich);
                    ps.setInt(3, idPedido);

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();

                        long totalPuntos = rs.getLong("puntos");

                        if (totalPuntos > Integer.MAX_VALUE) {
                            throw new SQLException(
                                    "El cálculo de puntos excede el límite permitido."
                            );
                        }

                        puntos = (int) totalPuntos;
                    }
                }

                // No registrar movimientos con cero puntos.
                if (puntos <= 0) {
                    con.rollback();
                    return 0;
                }

                // 6. Actualizar el saldo del cliente activo.
                String sqlCliente =
                        "UPDATE clientes SET puntos = puntos + ? "
                        + "WHERE id_cliente = ? AND activo = TRUE";

                try (PreparedStatement ps = con.prepareStatement(sqlCliente)) {
                    ps.setInt(1, puntos);
                    ps.setInt(2, idCliente);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "El cliente no existe o está inactivo."
                        );
                    }
                }

                // 7. Actualizar los puntos ganados del pedido.
                String sqlActualizarPedido =
                        "UPDATE pedidos SET puntos_ganados = ? "
                        + "WHERE id_pedido = ? AND puntos_ganados = 0";

                try (PreparedStatement ps =
                             con.prepareStatement(sqlActualizarPedido)) {

                    ps.setInt(1, puntos);
                    ps.setInt(2, idPedido);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudieron actualizar los puntos del pedido."
                        );
                    }
                }

                // 8. Registrar el movimiento de acumulación.
                MovimientoPuntos movimiento = new MovimientoPuntos();

                movimiento.setIdCliente(idCliente);
                movimiento.setPuntos(puntos);
                movimiento.setTipo(TipoMovimientoPuntos.ACUMULACION);
                movimiento.setIdPedido(idPedido);
                movimiento.setIdRecompensa(null);

                puntosDAO.registrarMovimiento(con, movimiento);

                // 9. Confirmar todas las operaciones.
                con.commit();

                return puntos;

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }
}


