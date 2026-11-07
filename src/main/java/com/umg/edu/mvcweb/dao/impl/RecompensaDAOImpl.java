
package com.umg.edu.mvcweb.dao.impl;

import com.umg.edu.mvcweb.dao.RecompensaDAO;
import com.umg.edu.mvcweb.modelo.Recompensa;
import com.umg.edu.mvcweb.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class RecompensaDAOImpl implements RecompensaDAO {

    @Override
    public List<Recompensa> listarActivas() throws SQLException {

        String sql = "SELECT id_recompensa, nombre, puntos_requeridos, "
                + "id_producto, id_menu, activa "
                + "FROM recompensas "
                + "WHERE activa = TRUE "
                + "ORDER BY puntos_requeridos ASC";

        List<Recompensa> recompensas = new ArrayList<>();

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Recompensa recompensa = mapearRecompensa(rs);
                recompensas.add(recompensa);
            }
        }

        return recompensas;
    }

    @Override
    public Recompensa buscarPorId(int id) throws SQLException {

        String sql = "SELECT id_recompensa, nombre, puntos_requeridos, "
                + "id_producto, id_menu, activa "
                + "FROM recompensas "
                + "WHERE id_recompensa = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearRecompensa(rs);
                }
            }
        }

        return null;
    }

    private Recompensa mapearRecompensa(ResultSet rs)
            throws SQLException {

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
