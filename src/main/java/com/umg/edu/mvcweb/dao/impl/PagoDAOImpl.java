
package com.umg.edu.mvcweb.dao.impl;

import com.umg.edu.mvcweb.dao.PagoDAO;
import com.umg.edu.mvcweb.modelo.Pago;
import com.umg.edu.mvcweb.modelo.MetodoPago;
import com.umg.edu.mvcweb.modelo.EstadoPago;
import com.umg.edu.mvcweb.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class PagoDAOImpl implements PagoDAO {

    @Override
    public void registrar(Connection con, Pago pago) throws SQLException {

        String sql = "INSERT INTO pagos "
                + "(id_pedido, metodo, monto, monto_recibido, cambio, "
                + "estado, referencia) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, pago.getIdPedido());
            ps.setString(2, pago.getMetodo().name());
            ps.setDouble(3, pago.getMonto());

            if (pago.getMontoRecibido() != null) {
                ps.setDouble(4, pago.getMontoRecibido());
            } else {
                ps.setNull(4, java.sql.Types.DECIMAL);
            }

            if (pago.getCambio() != null) {
                ps.setDouble(5, pago.getCambio());
            } else {
                ps.setNull(5, java.sql.Types.DECIMAL);
            }

            ps.setString(6, pago.getEstado().name());
            ps.setString(7, pago.getReferencia());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    pago.setId(rs.getInt(1));
                } else {
                    throw new SQLException(
                            "No se pudo recuperar el ID del pago.");
                }
            }
        }

        String sqlFecha = "SELECT fecha FROM pagos WHERE id_pago = ?";

        try (PreparedStatement psFecha = con.prepareStatement(sqlFecha)) {

            psFecha.setInt(1, pago.getId());

            try (ResultSet rsFecha = psFecha.executeQuery()) {

                if (rsFecha.next()) {

                    Timestamp fecha = rsFecha.getTimestamp("fecha");

                    if (fecha != null) {
                        pago.setFecha(fecha.toLocalDateTime());
                    }
                }
            }
        }
    }

    @Override
    public List<Pago> listarPorPedido(int idPedido) throws SQLException {

        String sql = "SELECT * FROM pagos WHERE id_pedido = ? "
                + "ORDER BY fecha DESC";

        List<Pago> pagos = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPedido);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Pago pago = new Pago();

                    pago.setId(rs.getInt("id_pago"));
                    pago.setIdPedido(rs.getInt("id_pedido"));

                    pago.setMetodo(
                            MetodoPago.valueOf(rs.getString("metodo")));

                    pago.setMonto(rs.getDouble("monto"));

                    double recibido = rs.getDouble("monto_recibido");

                    pago.setMontoRecibido(
                            rs.wasNull() ? null : recibido);

                    double cambio = rs.getDouble("cambio");

                    pago.setCambio(
                            rs.wasNull() ? null : cambio);

                    pago.setEstado(
                            EstadoPago.valueOf(rs.getString("estado")));

                    pago.setReferencia(rs.getString("referencia"));

                    Timestamp fecha = rs.getTimestamp("fecha");

                    if (fecha != null) {
                        pago.setFecha(fecha.toLocalDateTime());
                    }

                    pagos.add(pago);
                }
            }
        }

        return pagos;
    }
}
