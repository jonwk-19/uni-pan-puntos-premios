
package com.panpuntos.dao.impl;

import com.panpuntos.dao.MenuDAO;
import com.panpuntos.dao.ProductoDAO;
import com.panpuntos.modelo.Menu;
import com.panpuntos.modelo.Producto;
import com.panpuntos.modelo.Sandwich;
import com.panpuntos.modelo.Bebida;
import com.panpuntos.modelo.Acompanamiento;
import com.panpuntos.modelo.Categoria;
import com.panpuntos.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuDAOImpl implements MenuDAO {

    private final ProductoDAO productoDAO = new ProductoDAOImpl();

    private Menu mapear(ResultSet rs) throws SQLException {
        Menu menu = new Menu();

        menu.setId(rs.getInt("id_menu"));
        menu.setCodigo(rs.getString("codigo"));
        menu.setNombre(rs.getString("nombre"));
        menu.setPrecio(rs.getDouble("precio"));
        menu.setActivo(rs.getBoolean("activo"));

        Producto sandwich = productoDAO.buscarPorId(
                rs.getInt("id_sandwich"));
        Producto bebida = productoDAO.buscarPorId(
                rs.getInt("id_bebida"));
        Producto acompanamiento = productoDAO.buscarPorId(
                rs.getInt("id_acompanamiento"));

        if (sandwich == null || bebida == null || acompanamiento == null) {
            throw new SQLException(
                    "No se encontraron todos los componentes del menú "
                    + menu.getNombre());
        }

        if (sandwich.getCategoria() != Categoria.SANDWICH
                || bebida.getCategoria() != Categoria.BEBIDA
                || acompanamiento.getCategoria() != Categoria.ACOMPANAMIENTO) {
            throw new SQLException(
                    "Los componentes del menú tienen categorías incorrectas.");
        }

        menu.setSandwich((Sandwich) sandwich);
        menu.setBebida((Bebida) bebida);
        menu.setAcompanamiento((Acompanamiento) acompanamiento);

        return menu;
    }

    @Override
    public List<Menu> listarActivos() throws SQLException {
        String sql = "SELECT * FROM menus WHERE activo = TRUE ORDER BY nombre";

        List<Menu> menus = new ArrayList<>();

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
        String sql = "SELECT * FROM menus WHERE id_menu = ?";

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
}
