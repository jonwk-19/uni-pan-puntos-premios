package com.umg.edu.mvcweb.servicio;

import com.umg.edu.mvcweb.dao.MenuDAO;
import com.umg.edu.mvcweb.dao.PedidoDAO;
import com.umg.edu.mvcweb.excepcion.NegocioException;
import com.umg.edu.mvcweb.modelo.Cliente;
import com.umg.edu.mvcweb.modelo.DetallePedido;
import com.umg.edu.mvcweb.modelo.EstadoPedido;
import com.umg.edu.mvcweb.modelo.Menu;
import com.umg.edu.mvcweb.modelo.Pedido;
import com.umg.edu.mvcweb.modelo.Producto;
import com.umg.edu.mvcweb.util.ConexionDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoDAO pedidoDAO;

    @Autowired
    private MenuDAO menuDAO;

    @Autowired
    private ClienteServicio clienteServicio;

    @Autowired
    private ProductoServicio productoServicio;

    public int registrarDesdeArticulos(
            int idCliente,
            List<String> articulos,
            List<Integer> cantidades) throws SQLException, NegocioException {

        Cliente cliente = clienteServicio.buscarPorId(idCliente);

        if (cliente == null || !cliente.isActivo()) {
            throw new NegocioException(
                    "El cliente seleccionado no existe o esta inactivo.");
        }

        if (articulos == null || cantidades == null
                || articulos.isEmpty()
                || articulos.size() != cantidades.size()) {

            throw new NegocioException("Debes agregar al menos un articulo.");
        }

        Pedido pedido = new Pedido();
        pedido.setIdCliente(idCliente);

        for (int i = 0; i < articulos.size(); i++) {
            pedido.agregarDetalle(
                    crearDetalle(articulos.get(i), cantidades.get(i)));
        }

        return registrar(pedido);
    }

    private DetallePedido crearDetalle(String articulo, int cantidad)
            throws SQLException, NegocioException {

        if (articulo == null || articulo.isBlank()) {
            throw new NegocioException("Debes seleccionar todos los articulos.");
        }

        if (cantidad <= 0) {
            throw new NegocioException("La cantidad debe ser mayor que cero.");
        }

        DetallePedido detalle = new DetallePedido();
        detalle.setCantidad(cantidad);

        try {
            if (articulo.startsWith("P:")) {

                Producto producto = productoServicio.buscarPorId(
                        Integer.parseInt(articulo.substring(2)));

                if (producto == null) {
                    throw new NegocioException(
                            "Uno de los productos seleccionados no existe.");
                }

                productoServicio.validarVenta(producto, cantidad);

                detalle.setIdProducto(producto.getId());
                detalle.setNombre(producto.getNombre());
                detalle.setPrecioUnitario(producto.getPrecio());

            } else if (articulo.startsWith("M:")) {

                Menu menu = menuDAO.buscarPorId(
                        Integer.parseInt(articulo.substring(2)));

                if (menu == null || !menu.isActivo()) {
                    throw new NegocioException(
                            "Uno de los menus seleccionados no existe o esta inactivo.");
                }

                if (!menu.estaDisponible(cantidad)) {
                    throw new NegocioException(
                            "No hay suficiente existencia para el menu: "
                            + menu.getNombre());
                }

                detalle.setIdMenu(menu.getId());
                detalle.setNombre(menu.getNombre());
                detalle.setPrecioUnitario(menu.getPrecio());

            } else {
                throw new NegocioException(
                        "El articulo seleccionado no es valido.");
            }

        } catch (NumberFormatException e) {
            throw new NegocioException("El articulo seleccionado no es valido.");
        }

        return detalle;
    }

    public int registrar(Pedido pedido) throws SQLException {

        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser nulo.");
        }

        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new IllegalArgumentException(
                    "El pedido debe tener al menos un producto o menú.");
        }

        double total = pedido.calcularTotal();

        if (total <= 0) {
            throw new IllegalArgumentException(
                    "El total del pedido debe ser mayor que cero.");
        }

        try (Connection con = ConexionDB.getConexion()) {
            con.setAutoCommit(false);

            try {
                pedido.setTotal(total);
                pedido.setEstado(EstadoPedido.PENDIENTE);
                pedido.setPuntosGanados(0);

                int idPedido = pedidoDAO.crear(con, pedido);

                con.commit();
                return idPedido;

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public List<Pedido> listar() throws SQLException {
        return pedidoDAO.listar();
    }

    public List<Pedido> listarPorCliente(int idCliente)
            throws SQLException {

        if (idCliente <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del cliente no es válido.");
        }

        return pedidoDAO.listarPorCliente(idCliente);
    }

    public Pedido buscarPorId(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del pedido no es válido.");
        }

        return pedidoDAO.buscarPorId(id);
    }

    public void entregar(int idPedido) throws SQLException {

        cambiarEstado(idPedido, EstadoPedido.PAGADO,
                EstadoPedido.ENTREGADO);
    }

    public void anular(int idPedido) throws SQLException {

        if (idPedido <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del pedido no es válido.");
        }

        try (Connection con = ConexionDB.getConexion()) {
            con.setAutoCommit(false);

            try {
                Pedido pedido = bloquearPedido(con, idPedido);

                if (pedido == null) {
                    throw new IllegalArgumentException(
                            "El pedido no existe.");
                }

                if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
                    throw new IllegalArgumentException(
                            "Solo se pueden anular pedidos pendientes.");
                }

                pedidoDAO.actualizarEstado(
                        con, idPedido, EstadoPedido.ANULADO);

                con.commit();

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    private void cambiarEstado(int idPedido,
            EstadoPedido estadoActual,
            EstadoPedido estadoNuevo) throws SQLException {

        if (idPedido <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del pedido no es válido.");
        }

        try (Connection con = ConexionDB.getConexion()) {
            con.setAutoCommit(false);

            try {
                Pedido pedido = bloquearPedido(con, idPedido);

                if (pedido == null) {
                    throw new IllegalArgumentException(
                            "El pedido no existe.");
                }

                if (pedido.getEstado() != estadoActual) {
                    throw new IllegalArgumentException(
                            "El pedido no tiene el estado necesario para esta operación.");
                }

                pedidoDAO.actualizarEstado(
                        con, idPedido, estadoNuevo);

                con.commit();

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    private Pedido bloquearPedido(Connection con, int idPedido)
        throws SQLException {

    String sql = "SELECT * FROM pedidos "
            + "WHERE id_pedido = ? FOR UPDATE";

    try (java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idPedido);

        try (java.sql.ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                Pedido pedido = new Pedido();

                pedido.setId(rs.getInt("id_pedido"));

                int idCliente = rs.getInt("id_cliente");
                pedido.setIdCliente(rs.wasNull() ? null : idCliente);

                pedido.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                pedido.setEstado(
                        EstadoPedido.valueOf(rs.getString("estado")));
                pedido.setTotal(rs.getDouble("total"));
                pedido.setPuntosGanados(rs.getInt("puntos_ganados"));

                return pedido;
            }
        }
    }

    return null;
    }
}