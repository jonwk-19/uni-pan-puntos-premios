
package com.umg.edu.mvcweb.controller;

import com.umg.edu.mvcweb.modelo.EstadoPago;
import com.umg.edu.mvcweb.modelo.EstadoPedido;
import com.umg.edu.mvcweb.modelo.MetodoPago;
import com.umg.edu.mvcweb.modelo.Pago;
import com.umg.edu.mvcweb.modelo.Pedido;
import com.umg.edu.mvcweb.servicio.PagoService;
import com.umg.edu.mvcweb.servicio.PedidoService;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/pagos")
    public String mostrarFormulario(
            @RequestParam int id,
            Model model) throws SQLException {

        Pedido pedido = pedidoService.buscarPorId(id);

        if (pedido == null) {
            model.addAttribute("error", "El pedido no existe.");
            return "pedidos/lista";
        }

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            model.addAttribute("error", "El pedido no está pendiente de pago.");
            return "pedidos/lista";
        }

        model.addAttribute("pedido", pedido);
        model.addAttribute("metodosPago", MetodoPago.values());

        return "pagos/formulario";
    }

    @PostMapping("/pagos/procesar")
    public String procesarPago(
            @RequestParam int idPedido,
            @RequestParam MetodoPago metodo,
            @RequestParam(required = false) Double montoRecibido,
            @RequestParam(required = false, defaultValue = "true") boolean tarjetaAprobada,
            Model model) throws SQLException {

        Pedido pedido = pedidoService.buscarPorId(idPedido);

        if (pedido == null) {
            model.addAttribute("error", "El pedido no existe.");
            return "pedidos/lista";
        }

        try {
            Pago pago = pagoService.procesarPago(
                    pedido,
                    metodo,
                    montoRecibido,
                    tarjetaAprobada
            );

            model.addAttribute("pedido", pedido);
            model.addAttribute("pago", pago);

            if (pago.getEstado() == EstadoPago.RECHAZADO) {
                model.addAttribute("error", "El pago con tarjeta fue rechazado.");
                model.addAttribute("metodosPago", MetodoPago.values());
                return "pagos/formulario";
            }

            return "pagos/comprobante";

        } catch (IllegalArgumentException | SQLException e) {

            model.addAttribute("pedido", pedido);
            model.addAttribute("metodosPago", MetodoPago.values());
            model.addAttribute("error", e.getMessage());

            return "pagos/formulario";
        }
    }
}
