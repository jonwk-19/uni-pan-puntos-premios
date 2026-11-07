
package com.umg.edu.mvcweb.dao.impl;

import com.umg.edu.mvcweb.dao.ProductoDAO;
import com.umg.edu.mvcweb.modelo.Categoria;
import com.umg.edu.mvcweb.modelo.Producto;
import com.umg.edu.mvcweb.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements ProductoDAO {

    private static final String SELECT =
            "SELECT id_producto, codigo, categoria, nombre, precio, existencia, activo FROM productos";

    @Override
    public List<Producto> listar() throws SQLException {
        return consultar(SELECT + " ORDER BY categoria, nombre");
    }

    @Override
    public List<Producto> listarPorCategoria(Categoria categoria) throws SQLException {
        return consultar(
                SELECT + " WHERE categoria = ? ORDER BY nombre",
                categoria.name()
        );
    }

    @Override
    public List<Producto> listarBajoStock(int limite) throws SQLException {
        return consultar(
                SELECT + " WHERE activo = TRUE AND existencia <= ? ORDER BY existencia, nombre",
                limite
        );
    }

    @Override
    public Producto buscarPorId(int id) throws SQLException {
        List<Producto> encontrados =
                consultar(SELECT + " WHERE id_producto = ?", id);

        return encontrados.isEmpty() ? null : encontrados.get(0);
    }

    @Override
    public Producto buscarPorCodigo(String codigo) throws SQLException {
        List<Producto> encontrados =
                consultar(SELECT + " WHERE codigo = ?", codigo);

        return encontrados.isEmpty() ? null : encontrados.get(0);
    }

    @Override
    public void insertar(Producto producto) throws SQLException {

        String sql = "INSERT INTO productos "
                + "(codigo, categoria, nombre, precio, existencia, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getCategoria().name());
            ps.setString(3, producto.getNombre());
            ps.setDouble(4, producto.getPrecio());
            ps.setInt(5, producto.getExistencia());
            ps.setBoolean(6, producto.isActivo());

            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    producto.setId(claves.getInt(1));
                }
            }
        }
    }

    @Override
    public void actualizar(Producto producto) throws SQLException {

        String sql = "UPDATE productos "
                + "SET codigo = ?, nombre = ?, precio = ? "
                + "WHERE id_producto = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setDouble(3, producto.getPrecio());
            ps.setInt(4, producto.getId());

            ps.executeUpdate();
        }
    }

    @Override
    public void cambiarEstado(int id, boolean activo) throws SQLException {

        String sql =
                "UPDATE productos SET activo = ? WHERE id_producto = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, activo);
            ps.setInt(2, id);

            ps.executeUpdate();
        }
    }

    @Override
    public boolean descontarExistencia(
            Connection con,
            int idProducto,
            int cantidad
    ) throws SQLException {

        if (con == null || idProducto <= 0 || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La conexion, el producto y la cantidad deben ser validos."
            );
        }

        String sql = "UPDATE productos "
                + "SET existencia = existencia - ? "
                + "WHERE id_producto = ? "
                + "AND existencia >= ? "
                + "AND activo = TRUE";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cantidad);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidad);

            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public void aumentarExistencia(
            Connection con,
            int idProducto,
            int cantidad
    ) throws SQLException {

        if (con == null || idProducto <= 0 || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La conexion, el producto y la cantidad deben ser validos."
            );
        }

        String sql = "UPDATE productos "
                + "SET existencia = existencia + ? "
                + "WHERE id_producto = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cantidad);
            ps.setInt(2, idProducto);

            if (ps.executeUpdate() != 1) {
                throw new SQLException(
                        "No se pudo aumentar la existencia del producto."
                );
            }
        }
    }

    private List<Producto> consultar(
            String sql,
            Object... parametros
    ) throws SQLException {

        List<Producto> productos = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    productos.add(mapear(rs));
                }
            }
        }

        return productos;
    }

    private Producto mapear(ResultSet rs) throws SQLException {

        Producto p = Producto.crear(
                Categoria.valueOf(rs.getString("categoria"))
        );

        p.setId(rs.getInt("id_producto"));
        p.setCodigo(rs.getString("codigo"));
        p.setNombre(rs.getString("nombre"));
        p.setPrecio(rs.getDouble("precio"));
        p.setExistencia(rs.getInt("existencia"));
        p.setActivo(rs.getBoolean("activo"));

        return p;
    }
}
  