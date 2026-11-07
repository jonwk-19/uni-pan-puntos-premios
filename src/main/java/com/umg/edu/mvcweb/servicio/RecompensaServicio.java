package com.umg.edu.mvcweb.servicio;

import com.umg.edu.mvcweb.dao.impl.MenuDAOImpl;
import com.umg.edu.mvcweb.dao.impl.ProductoDAOImpl;
import com.umg.edu.mvcweb.dao.impl.PuntosDAOImpl;
import com.umg.edu.mvcweb.modelo.Menu;
import com.umg.edu.mvcweb.modelo.MovimientoPuntos;
import com.umg.edu.mvcweb.modelo.Producto;
import com.umg.edu.mvcweb.modelo.Recompensa;
import com.umg.edu.mvcweb.modelo.TipoMovimientoPuntos;
import com.umg.edu.mvcweb.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecompensaServicio {

    private final PuntosDAOImpl puntosDAO = new PuntosDAOImpl();
    private final ProductoDAOImpl productoDAO = new ProductoDAOImpl();
    private final MenuDAOImpl menuDAO = new MenuDAOImpl();

    public boolean canjear(int idCliente, int idRecompensa)
            throws SQLException {

        if (idCliente <= 0 || idRecompensa <= 0) {
            throw new IllegalArgumentException(
                    "Los identificadores deben ser validos."
            );
        }

        try (Connection con = ConexionDB.getConexion()) {

            con.setAutoCommit(false);

            try {
                int puntosCliente = consultarPuntosCliente(con, idCliente);

                Recompensa recompensa =
                        buscarRecompensa(con, idRecompensa);

                if (recompensa == null || !recompensa.isActiva()) {
                    throw new SQLException(
                            "La recompensa no existe o esta inactiva."
                    );
                }

                int costo = recompensa.getPuntosRequeridos();

                if (costo <= 0) {
                    throw new SQLException(
                            "El costo de la recompensa no es valido."
                    );
                }

                if (puntosCliente < costo) {
                    con.rollback();
                    return false;
                }

                // Descontar y validar el producto o menu.
                if (recompensa.getIdProducto() != null) {

                    descontarProducto(
                            con,
                            recompensa.getIdProducto()
                    );

                } else if (recompensa.getIdMenu() != null) {

                    descontarMenu(
                            con,
                            recompensa.getIdMenu()
                    );

                } else {
                    throw new SQLException(
                            "La recompensa no tiene un producto o menu."
                    );
                }

           
                String sqlDescuento =
                        "UPDATE clientes SET puntos = puntos - ? "
                        + "WHERE id_cliente = ? "
                        + "AND activo = TRUE AND puntos >= ?";

                try (PreparedStatement ps =
                             con.prepareStatement(sqlDescuento)) {

                    ps.setInt(1, costo);
                    ps.setInt(2, idCliente);
                    ps.setInt(3, costo);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudieron descontar los puntos."
                        );
                    }
                }

            
                MovimientoPuntos movimiento = new MovimientoPuntos();
                movimiento.setIdCliente(idCliente);
                movimiento.setPuntos(-costo);
                movimiento.setTipo(TipoMovimientoPuntos.CANJE);
                movimiento.setIdPedido(null);
                movimiento.setIdRecompensa(idRecompensa);

                puntosDAO.registrarMovimiento(con, movimiento);

                con.commit();
                return true;

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    private int consultarPuntosCliente(Connection con, int idCliente)
            throws SQLException {

        String sql =
                "SELECT puntos FROM clientes "
                + "WHERE id_cliente = ? AND activo = TRUE "
                + "FOR UPDATE";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException(
                            "El cliente no existe o esta inactivo."
                    );
                }

                return rs.getInt("puntos");
            }
        }
    }

    private Recompensa buscarRecompensa(
            Connection con, int idRecompensa) throws SQLException {

        String sql =
                "SELECT id_recompensa, nombre, puntos_requeridos, "
                + "id_producto, id_menu, activa "
                + "FROM recompensas "
                + "WHERE id_recompensa = ? "
                + "FOR UPDATE";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRecompensa);

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

    private void descontarProducto(
            Connection con, int idProducto) throws SQLException {

        if (!productoDAO.descontarExistencia(con, idProducto, 1)) {
            throw new SQLException(
                    "El producto esta inactivo o no tiene existencias."
            );
        }
    }

    private void descontarMenu(
            Connection con, int idMenu) throws SQLException {

        // Bloquear y validar el menu.
        String sql =
                "SELECT activo FROM menus "
                + "WHERE id_menu = ? FOR UPDATE";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMenu);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next() || !rs.getBoolean("activo")) {
                    throw new SQLException(
                            "El menu no existe o esta inactivo."
                    );
                }
            }
        }

        Menu menu = menuDAO.buscarPorId(con, idMenu);

        if (menu == null || !menu.estaDisponible(1)) {
            throw new SQLException(
                    "El menu o alguno de sus componentes no esta disponible."
            );
        }

      
        for (Producto componente : menu.getComponentes()) {
            descontarProducto(con, componente.getId());
        }
    }
}
