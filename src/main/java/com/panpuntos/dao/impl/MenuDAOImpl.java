package com.panpuntos.dao.impl;

import com.panpuntos.dao.MenuDAO;
import com.panpuntos.modelo.Menu;
import com.panpuntos.modelo.Producto;
import com.panpuntos.modelo.Categoria;
import com.panpuntos.modelo.Sandwich;
import com.panpuntos.modelo.Bebida;
import com.panpuntos.modelo.Acompanamiento;
import com.panpuntos.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuDAOImpl implements MenuDAO {

    private static final String SELECT_MENU =
            "SELECT m.id_menu, m.codigo, m.nombre, m.precio, m.activo, "
            + "s.id_producto AS s_id, s.codigo AS s_codigo, "
            + "s.categoria AS s_categoria, s.nombre AS s_nombre, "
            + "s.precio AS s_precio, s.existencia AS s_existencia, "
            + "s.activo AS s_activo, "
            + "b.id_producto AS b_id, b.codigo AS b_codigo, "
            + "b.categoria AS b_categoria, b.nombre AS b_nombre, "
            + "b.precio AS b_precio, b.existencia AS b_existencia, "
            + "b.activo AS b_activo, "
            + "a.id_producto AS a_id, a.codigo AS a_codigo, "
            + "a.categoria AS a_categoria, a.nombre AS a_nombre, "
            + "a.precio AS a_precio, a.existencia AS a_existencia, "
            + "a.activo AS a_activo "
            + "FROM menus m "
            + "INNER JOIN productos s ON m.id_sandwich = s.id_producto "
            + "INNER JOIN productos b ON m.id_bebida = b.id_producto "
            + "INNER JOIN productos a ON m.id_acompanamiento = a.id_producto ";

    @Override
    public List<Menu> listarActivos() throws SQLException {
        List<Menu> menus = new ArrayList<>();
        String sql = SELECT_MENU + "WHERE m.activo = TRUE ORDER BY m.nombre";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                menus.add(mapear(rs));
            }
        }
        return menus;
    }

    @Override
    public Menu buscarPorId(int id) throws SQLException {
        try (Connection con = ConexionDB.getConexion()) {
            return buscarPorId(con, id);
        }
    }

    @Override
    public Menu buscarPorId(Connection con, int id) throws SQLException {
        String sql = SELECT_MENU + "WHERE m.id_menu = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    private Menu mapear(ResultSet rs) throws SQLException {
        Menu menu = new Menu();
        menu.setId(rs.getInt("id_menu"));
        menu.setCodigo(rs.getString("codigo"));
        menu.setNombre(rs.getString("nombre"));
        menu.setPrecio(rs.getDouble("precio"));
        menu.setActivo(rs.getBoolean("activo"));

        Producto sandwich = mapearProducto(rs, "s");
        Producto bebida = mapearProducto(rs, "b");
        Producto acompanamiento = mapearProducto(rs, "a");

        if (sandwich.getCategoria() != Categoria.SANDWICH
                || bebida.getCategoria() != Categoria.BEBIDA
                || acompanamiento.getCategoria() != Categoria.ACOMPANAMIENTO) {
            throw new SQLException("Los componentes del menu tienen categorias incorrectas.");
        }

        menu.setSandwich((Sandwich) sandwich);
        menu.setBebida((Bebida) bebida);
        menu.setAcompanamiento((Acompanamiento) acompanamiento);
        return menu;
    }

    private Producto mapearProducto(ResultSet rs, String prefijo) throws SQLException {
        Categoria categoria = Categoria.valueOf(rs.getString(prefijo + "_categoria"));
        Producto producto = Producto.crear(categoria);

        producto.setId(rs.getInt(prefijo + "_id"));
        producto.setCodigo(rs.getString(prefijo + "_codigo"));
        producto.setNombre(rs.getString(prefijo + "_nombre"));
        producto.setPrecio(rs.getDouble(prefijo + "_precio"));
        producto.setExistencia(rs.getInt(prefijo + "_existencia"));
        producto.setActivo(rs.getBoolean(prefijo + "_activo"));

        return producto;
    }
}
