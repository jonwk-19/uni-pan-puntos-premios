
package com.panpuntos.dao.impl;

import com.panpuntos.dao.ClienteDAO;
import com.panpuntos.modelo.Cliente;
import com.panpuntos.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl implements ClienteDAO {

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();

        c.setId(rs.getInt("id_cliente"));
        c.setDocumento(rs.getString("documento"));
        c.setNombre(rs.getString("nombre"));
        c.setApellidos(rs.getString("apellidos"));
        c.setTelefono(rs.getString("telefono"));
        c.setEmail(rs.getString("email"));
        c.setPuntos(rs.getInt("puntos"));
        c.setActivo(rs.getBoolean("activo"));

        java.sql.Date fecha = rs.getDate("fecha_registro");
        if (fecha != null) {
            c.setFechaRegistro(fecha.toLocalDate());
        }

        return c;
    }

    @Override
    public List<Cliente> listar() throws SQLException {
        String sql = "SELECT * FROM clientes ORDER BY nombre";
        List<Cliente> lista = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }

        return lista;
    }

    @Override
    public List<Cliente> buscarPorNombre(String texto)
            throws SQLException {

        String sql = "SELECT * FROM clientes "
                + "WHERE CONCAT(nombre, ' ', apellidos) LIKE ? "
                + "ORDER BY nombre";

        List<Cliente> lista = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + texto + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }

        return lista;
    }

    @Override
    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }

        return null;
    }

    @Override
    public Cliente buscarPorDocumento(String documento)
            throws SQLException {

        String sql = "SELECT * FROM clientes WHERE documento = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, documento);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }

        return null;
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
                + "apellidos = ?, telefono = ?, email = ?, puntos = ?, "
                + "activo = ? WHERE id_cliente = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cliente.getDocumento());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getApellidos());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getEmail());
            ps.setInt(6, cliente.getPuntos());
            ps.setBoolean(7, cliente.isActivo());
            ps.setInt(8, cliente.getId());

            ps.executeUpdate();
        }
    }

    @Override
    public void cambiarEstado(int id, boolean activo)
            throws SQLException {

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
        String sql = "SELECT COUNT(*) FROM pedidos WHERE id_cliente = ?";

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
                + "WHERE id_cliente = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, puntos);
            ps.setInt(2, idCliente);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró el cliente.");
            }
        }
    }

    @Override
    public boolean restarPuntos(Connection con, int idCliente, int puntos)
            throws SQLException {

        String sql = "UPDATE clientes SET puntos = puntos - ? "
                + "WHERE id_cliente = ? AND puntos >= ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, puntos);
            ps.setInt(2, idCliente);
            ps.setInt(3, puntos);

            return ps.executeUpdate() > 0;
        }
    }
}
