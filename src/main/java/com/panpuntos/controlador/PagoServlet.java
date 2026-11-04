
package com.panpuntos.controlador;

import com.panpuntos.modelo.MetodoPago;
import com.panpuntos.modelo.Pedido;
import com.panpuntos.modelo.Pago;
import com.panpuntos.servicio.PagoService;
import com.panpuntos.servicio.PedidoService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/pagos")
public class PagoServlet extends ControladorBase {

    private final PagoService pagoService = new PagoService();
    private final PedidoService pedidoService = new PedidoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            int idPedido = Integer.parseInt(req.getParameter("id"));

            Pedido pedido = pedidoService.buscarPorId(idPedido);

            if (pedido == null) {
                mostrarError(req, resp, "El pedido no existe.");
                return;
            }

            req.setAttribute("pedido", pedido);
            req.setAttribute("metodos", MetodoPago.values());

            irA(req, resp, "pagos/formulario");

        } catch (NumberFormatException e) {
            mostrarError(req, resp, "El identificador del pedido no es válido.");
        } catch (SQLException e) {
            mostrarError(req, resp, "Error de base de datos: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            int idPedido = Integer.parseInt(req.getParameter("idPedido"));

            MetodoPago metodo = MetodoPago.valueOf(
                    req.getParameter("metodo"));

            Double montoRecibido = null;

            if (metodo == MetodoPago.EFECTIVO) {
                montoRecibido = Double.parseDouble(
                        req.getParameter("montoRecibido").replace(',', '.'));
            }

            Pedido pedido = pedidoService.buscarPorId(idPedido);

            if (pedido == null) {
                mostrarError(req, resp, "El pedido no existe.");
                return;
            }

            Pago pago = pagoService.procesarPago(
                    pedido, metodo, montoRecibido);

            req.setAttribute("pago", pago);
            req.setAttribute("pedido", pedido);

            irA(req, resp, "pagos/comprobante");

        } catch (NumberFormatException | NullPointerException e) {
            mostrarError(req, resp, "Verifica los datos del pago.");
        } catch (IllegalArgumentException e) {
            mostrarError(req, resp, e.getMessage());
        } catch (SQLException e) {
            mostrarError(req, resp, "No se pudo procesar el pago: " + e.getMessage());
        }
    }
}
