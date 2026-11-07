
package com.umg.edu.mvcweb.dao.impl;

import com.umg.edu.mvcweb.dao.ClienteDAO;
import com.umg.edu.mvcweb.modelo.Cliente;
import com.umg.edu.mvcweb.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ClienteDAOImpl implements ClienteDAO {

    private static final String SELECT =
            "SELECT id_cliente, documento, nombre, apellidos, telefono, email, puntos, activo, fecha_registro FROM clientes";

    @Override
    public List<Cliente> listar() throws SQLException {
        return consultar(SELECT + " ORDER BY nombre, apellidos");
    }

    @Override
    public List<Cliente> buscarPorNombre(String texto) throws SQLException {
        String sql = SELECT
                + " WHERE nombre LIKE ? OR apellidos LIKE ? OR CONCAT(nombre, ' ', apellidos) LIKE ?"
                + " ORDER BY nombre, apellidos";

        String busqueda = "%" + texto + "%";

        List<Cliente> clientes = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, busqueda);
            ps.setString(2, busqueda);
            ps.setString(3, busqueda);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapear(rs));
                }
            }
        }

        return clientes;
    }

    @Override
    public Cliente buscarPorId(int id) throws SQLException {
        List<Cliente> clientes =
                consultar(SELECT + " WHERE id_cliente = ?", id);

        return clientes.isEmpty() ? null : clientes.get(0);
    }

    @Override
    public Cliente buscarPorDocumento(String documento) throws SQLException {
        List<Cliente> clientes =
                consultar(SELECT + " WHERE documento = ?", documento);

        return clientes.isEmpty() ? null : clientes.get(0);
    }

    @Override
    public void insertar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes "
                + "(documento, nombre, apellidos, telefono, email, puntos, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getDocumento());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getApellidos());
            ps.setString(4, cliente.getTelefono());

            if (cliente.getEmail() == null) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, cliente.getEmail());
            }

            ps.setInt(6, cliente.getPuntos());
            ps.setBoolean(7, cliente.isActivo());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET documento = ?, nombre = ?, "
                + "apellidos = ?, telefono = ?, email = ? "
                + "WHERE id_cliente = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cliente.getDocumento());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getApellidos());
            ps.setString(4, cliente.getTelefono());

            if (cliente.getEmail() == null) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, cliente.getEmail());
            }

            ps.setInt(6, cliente.getId());

            ps.executeUpdate();
        }
    }

    @Override
    public void cambiarEstado(int id, boolean activo) throws SQLException {
        String sql = "UPDATE clientes SET activo = ? WHERE id_cliente = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, id);

            ps.executeUpdate();
        }
    }

    @Override
    public boolean tieneCompras(int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pedidos "
                + "WHERE id_cliente = ? "
                + "AND estado IN ('PAGADO', 'ENTREGADO')";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    @Override
    public void sumarPuntos(Connection con, int idCliente, int puntos)
            throws SQLException {

        String sql = "UPDATE clientes SET puntos = puntos + ? "
                + "WHERE id_cliente = ? AND activo = TRUE";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, puntos);
            ps.setInt(2, idCliente);

            if (ps.executeUpdate() == 0) {
                throw new SQLException(
                        "No se encontró un cliente activo para acreditar puntos.");
            }
        }
    }

    @Override
    public boolean restarPuntos(Connection con, int idCliente, int puntos)
            throws SQLException {

        String sql = "UPDATE clientes SET puntos = puntos - ? "
                + "WHERE id_cliente = ? AND activo = TRUE AND puntos >= ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, puntos);
            ps.setInt(2, idCliente);
            ps.setInt(3, puntos);

            return ps.executeUpdate() > 0;
        }
    }

    private List<Cliente> consultar(String sql, Object... parametros)
            throws SQLException {

        List<Cliente> clientes = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapear(rs));
                }
            }
        }

        return clientes;
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();

        cliente.setId(rs.getInt("id_cliente"));
        cliente.setDocumento(rs.getString("documento"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setApellidos(rs.getString("apellidos"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setEmail(rs.getString("email"));
        cliente.setPuntos(rs.getInt("puntos"));
        cliente.setActivo(rs.getBoolean("activo"));

        Date fecha = rs.getDate("fecha_registro");

        if (fecha != null) {
            cliente.setFechaRegistro(fecha.toLocalDate());
        }

        return cliente;
    }
}
    