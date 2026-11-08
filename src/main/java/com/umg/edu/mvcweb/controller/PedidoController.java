package com.umg.edu.mvcweb.controller;

import com.umg.edu.mvcweb.dao.MenuDAO;
import com.umg.edu.mvcweb.excepcion.NegocioException;
import com.umg.edu.mvcweb.modelo.Cliente;
import com.umg.edu.mvcweb.modelo.Pedido;
import com.umg.edu.mvcweb.servicio.ClienteServicio;
import com.umg.edu.mvcweb.servicio.PedidoService;
import com.umg.edu.mvcweb.servicio.ProductoServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteServicio clienteServicio;

    @Autowired
    private ProductoServicio productoServicio;

    @Autowired
    private MenuDAO menuDAO;

    @GetMapping("/pedidos")
    public String mostrarPedidos(Model model) throws SQLException {

        Map<Integer, String> clientes = new HashMap<>();

        for (Cliente cliente : clienteServicio.listar()) {
            clientes.put(cliente.getId(), cliente.getNombreCompleto());
        }

        model.addAttribute("pedidos", pedidoService.listar());
        model.addAttribute("clientes", clientes);

        return "pedidos/lista";
    }

    @GetMapping("/pedidos/nuevo")
    public String nuevoPedido(Model model) throws SQLException {

        cargarFormulario(model);

        return "pedidos/formulario";
    }

    @PostMapping("/pedidos/crear")
    public String crearPedido(
            @RequestParam int idCliente,
            @RequestParam("articulo") List<String> articulos,
            @RequestParam("cantidad") List<Integer> cantidades,
            Model model,
            RedirectAttributes flash) throws SQLException {

        int idPedido;

        try {
            idPedido = pedidoService.registrarDesdeArticulos(
                    idCliente, articulos, cantidades);
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
            cargarFormulario(model);
            return "pedidos/formulario";
        }

        flash.addFlashAttribute(
                "mensaje",
                "Pedido #" + idPedido + " registrado correctamente");

        return "redirect:/pedidos";
    }

    @GetMapping("/pedidos/detalle/{id}")
    public String detallePedido(
            @PathVariable int id,
            Model model) throws SQLException, NegocioException {

        Pedido pedido = pedidoService.buscarPorId(id);

        if (pedido == null) {
            throw new NegocioException("El pedido no existe");
        }

        if (pedido.getIdCliente() != null) {
            model.addAttribute(
                    "cliente",
                    clienteServicio.buscarPorId(pedido.getIdCliente()));
        }

        model.addAttribute("pedido", pedido);

        return "pedidos/detalle";
    }

    @PostMapping("/pedidos/entregar/{id}")
    public String entregarPedido(
            @PathVariable int id,
            RedirectAttributes flash) throws SQLException {

        pedidoService.entregar(id);

        flash.addFlashAttribute("mensaje", "El pedido fue marcado como entregado");

        return "redirect:/pedidos";
    }

    @PostMapping("/pedidos/anular/{id}")
    public String anularPedido(
            @PathVariable int id,
            RedirectAttributes flash) throws SQLException {

        pedidoService.anular(id);

        flash.addFlashAttribute("mensaje", "El pedido fue anulado");

        return "redirect:/pedidos";
    }

    private void cargarFormulario(Model model) throws SQLException {

        model.addAttribute("clientes", clienteServicio.listarActivos());
        model.addAttribute("productos", productoServicio.listar());
        model.addAttribute("menus", menuDAO.listarActivos());
    }
}
