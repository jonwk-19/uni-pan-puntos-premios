
package com.umg.edu.mvcweb.controller;

import com.umg.edu.mvcweb.dao.PuntosDAO;
import com.umg.edu.mvcweb.modelo.Cliente;
import com.umg.edu.mvcweb.modelo.MovimientoPuntos;
import com.umg.edu.mvcweb.servicio.ClienteServicio;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HistorialPuntosController {

    @Autowired
    private PuntosDAO puntosDAO;

    @Autowired
    private ClienteServicio clienteServicio;

    @GetMapping("/clientes/historial/{id}")
    public String mostrarHistorial(
            @PathVariable int id, Model model) throws SQLException {

        Cliente cliente = clienteServicio.buscarPorId(id);

        if (cliente == null) {
            return "redirect:/clientes";
        }

        List<MovimientoPuntos> movimientos =
                puntosDAO.historialPorCliente(id);

        model.addAttribute("cliente", cliente);
        model.addAttribute("movimientos", movimientos);

        return "clientes/historial";
    }
}
