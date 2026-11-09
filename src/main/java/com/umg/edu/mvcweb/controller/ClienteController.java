
package com.umg.edu.mvcweb.controller;

import com.umg.edu.mvcweb.excepcion.NegocioException;
import com.umg.edu.mvcweb.modelo.Cliente;
import com.umg.edu.mvcweb.servicio.ClienteServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.List;

@Controller
public class ClienteController {

    @Autowired
    private ClienteServicio clienteServicio;

    @GetMapping("/clientes")
    public String mostrarClientes(
            @RequestParam(required = false) String buscar,
            Model model) throws SQLException {

        List<Cliente> clientes = (buscar == null || buscar.isBlank())
                ? clienteServicio.listar()
                : clienteServicio.buscarPorNombre(buscar);

        model.addAttribute("clientes", clientes);
        model.addAttribute("buscar", buscar);

        return "clientes/lista";
    }

    @GetMapping("/clientes/nuevo")
    public String nuevoCliente(Model model) {

        model.addAttribute("cliente", new Cliente());

        return prepararFormulario(model, false);
    }

    @PostMapping("/clientes/guardar")
    public String guardarCliente(
            @ModelAttribute Cliente cliente,
            Model model,
            RedirectAttributes flash) throws SQLException {

        try {
            clienteServicio.registrar(cliente);
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cliente", cliente);
            return prepararFormulario(model, false);
        }

        flash.addFlashAttribute("mensaje", "Cliente registrado correctamente");

        return "redirect:/clientes";
    }

    @GetMapping("/clientes/editar/{id}")
    public String editarCliente(
            @PathVariable int id,
            Model model) throws SQLException, NegocioException {

        Cliente cliente = clienteServicio.buscarPorId(id);

        if (cliente == null) {
            throw new NegocioException("El cliente no existe");
        }

        model.addAttribute("cliente", cliente);

        return prepararFormulario(model, true);
    }

    @PostMapping("/clientes/actualizar")
    public String actualizarCliente(
            @ModelAttribute Cliente cliente,
            Model model,
            RedirectAttributes flash) throws SQLException {

        try {
            clienteServicio.modificar(cliente);
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cliente", cliente);
            return prepararFormulario(model, true);
        }

        flash.addFlashAttribute("mensaje", "Cliente actualizado correctamente");

        return "redirect:/clientes";
    }

    @PostMapping("/clientes/estado/{id}")
    public String cambiarEstado(
            @PathVariable int id,
            @RequestParam boolean activo,
            RedirectAttributes flash) throws SQLException, NegocioException {

        clienteServicio.cambiarEstado(id, activo);

        flash.addFlashAttribute("mensaje", "Estado del cliente actualizado");

        return "redirect:/clientes";
    }

    private String prepararFormulario(Model model, boolean editando) {

        model.addAttribute("editando", editando);

        return "clientes/formulario";
    }
}

