
package com.panpuntos.controlador;

import com.panpuntos.dao.MenuDAO;
import com.panpuntos.dao.impl.MenuDAOImpl;
import com.panpuntos.excepcion.NegocioException;
import com.panpuntos.modelo.Cliente;
import com.panpuntos.modelo.DetallePedido;
import com.panpuntos.modelo.Menu;
import com.panpuntos.modelo.Pedido;
import com.panpuntos.modelo.Producto;
import com.panpuntos.servicio.ClienteService;
import com.panpuntos.servicio.PedidoService;
import com.panpuntos.servicio.ProductoServicio;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/pedidos")
public class PedidoServlet extends ControladorBase {

    private final PedidoService servicio = new PedidoService();
    private final ClienteService clienteServicio = new ClienteService();
    private final ProductoServicio productoServicio = new ProductoServicio();
    private final MenuDAO menuDAO = new MenuDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String accion = req.getParameter("accion");

        try {
            if ("nuevo".equals(accion)) {
                cargarFormulario(req, resp);

            } else if ("detalle".equals(accion)) {

                int id = Integer.parseInt(req.getParameter("id"));
                Pedido pedido = servicio.buscarPorId(id);

                if (pedido == null) {
                    mostrarError(req, resp, "El pedido no existe.");
                    return;
                }

                req.setAttribute("pedido", pedido);
                irA(req, resp, "pedidos/detalle");

            } else {
                req.setAttribute("pedidos", servicio.listar());
                irA(req, resp, "pedidos/lista");
            }

        } catch (SQLException e) {
            mostrarError(req, resp,
                    "Error de base de datos: " + e.getMessage());

        } catch (NumberFormatException e) {
            mostrarError(req, resp,
                    "El identificador del pedido no es válido.");

        } catch (IllegalArgumentException e) {
            mostrarError(req, resp, e.getMessage());
        }
    }

    private void cargarFormulario(HttpServletRequest req,
            HttpServletResponse resp)
            throws SQLException, ServletException, IOException {

        req.setAttribute("clientes", clienteServicio.listarActivos());
        req.setAttribute("productos", productoServicio.listar());
        req.setAttribute("menus", menuDAO.listarActivos());

        irA(req, resp, "pedidos/formulario");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String accion = req.getParameter("accion");

        try {
            if ("crear".equals(accion)) {
                crearPedido(req, resp);
                return;
            }

            int idPedido = Integer.parseInt(req.getParameter("id"));

            if ("entregar".equals(accion)) {
                servicio.entregar(idPedido);
                redirigir(req, resp, "/pedidos",
                        "El pedido fue marcado como entregado.");

            } else if ("anular".equals(accion)) {
                servicio.anular(idPedido);
                redirigir(req, resp, "/pedidos",
                        "El pedido fue anulado.");

            } else {
                resp.sendRedirect(req.getContextPath() + "/pedidos");
            }

        } catch (NumberFormatException e) {
            mostrarError(req, resp,
                    "Los datos numéricos del pedido no son válidos.");

        } catch (SQLException e) {
            mostrarError(req, resp,
                    "Error de base de datos: " + e.getMessage());

        } catch (NegocioException e) {
            mostrarError(req, resp, e.getMessage());

        } catch (IllegalArgumentException e) {
            mostrarError(req, resp, e.getMessage());
        }
    }

    private void crearPedido(HttpServletRequest req,
            HttpServletResponse resp)
            throws SQLException, ServletException, IOException, NegocioException {

        int idCliente = Integer.parseInt(req.getParameter("idCliente"));

        Cliente cliente = clienteServicio.buscarPorId(idCliente);

        if (cliente == null || !cliente.isActivo()) {
            throw new IllegalArgumentException(
                    "El cliente seleccionado no existe o está inactivo.");
        }

        String[] articulos = req.getParameterValues("articulo");
        String[] cantidades = req.getParameterValues("cantidad");

        if (articulos == null || cantidades == null
                || articulos.length != cantidades.length
                || articulos.length == 0) {

            throw new IllegalArgumentException(
                    "Debes agregar al menos un artículo.");
        }

        Pedido pedido = new Pedido();
        pedido.setIdCliente(idCliente);

        for (int i = 0; i < articulos.length; i++) {

            String articulo = articulos[i];
            int cantidad = Integer.parseInt(cantidades[i]);

            if (cantidad <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad debe ser mayor que cero.");
            }

            if (articulo == null || articulo.isBlank()) {
                throw new IllegalArgumentException(
                        "Debes seleccionar todos los artículos.");
            }

            DetallePedido detalle = new DetallePedido();

            if (articulo.startsWith("P:")) {

                int idProducto = Integer.parseInt(
                        articulo.substring(2));

                Producto producto =
                        productoServicio.buscarPorId(idProducto);

                if (producto == null) {
                    throw new IllegalArgumentException(
                            "Uno de los productos seleccionados no existe.");
                }

                productoServicio.validarVenta(producto, cantidad);

                detalle.setIdProducto(producto.getId());
                detalle.setIdMenu(null);
                detalle.setNombre(producto.getNombre());
                detalle.setPrecioUnitario(producto.getPrecio());

            } else if (articulo.startsWith("M:")) {

                int idMenu = Integer.parseInt(
                        articulo.substring(2));

                Menu menu = menuDAO.buscarPorId(idMenu);

                if (menu == null || !menu.isActivo()) {
                    throw new IllegalArgumentException(
                            "Uno de los menús seleccionados no existe o está inactivo.");
                }

                if (!menu.estaDisponible(cantidad)) {
                    throw new IllegalArgumentException(
                            "No hay suficiente existencia para el menú: "
                            + menu.getNombre());
                }

                detalle.setIdProducto(null);
                detalle.setIdMenu(menu.getId());
                detalle.setNombre(menu.getNombre());
                detalle.setPrecioUnitario(menu.getPrecio());

            } else {
                throw new IllegalArgumentException(
                        "El artículo seleccionado no es válido.");
            }

            detalle.setCantidad(cantidad);
            pedido.agregarDetalle(detalle);
        }

        int idPedido = servicio.registrar(pedido);

        redirigir(req, resp, "/pedidos",
                "Pedido #" + idPedido + " registrado correctamente.");
    }
}
