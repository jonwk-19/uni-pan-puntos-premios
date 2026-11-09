
package com.umg.edu.mvcweb.controller;

import com.umg.edu.mvcweb.dao.RecompensaDAO;
import com.umg.edu.mvcweb.modelo.Cliente;
import com.umg.edu.mvcweb.modelo.Recompensa;
import com.umg.edu.mvcweb.servicio.ClienteServicio;
import com.umg.edu.mvcweb.servicio.RecompensaServicio;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RecompensaController {

    @Autowired
    private RecompensaDAO recompensaDAO;

    @Autowired
    private ClienteServicio clienteServicio;

    @Autowired
    private RecompensaServicio recompensaServicio;

    @GetMapping("/recompensas")
    public String mostrarRecompensas(Model model) throws SQLException {

        List<Recompensa> recompensas = recompensaDAO.listarActivas();
        List<Cliente> clientes = clienteServicio.listarActivos();

        model.addAttribute("recompensas", recompensas);
        model.addAttribute("clientes", clientes);

        return "recompensas/lista";
    }

    @PostMapping("/recompensas/canjear")
    public String canjear(
            @RequestParam int idCliente,
            @RequestParam int idRecompensa,
            RedirectAttributes flash) {

        try {
            boolean realizado = recompensaServicio.canjear(
                    idCliente, idRecompensa);

            if (realizado) {
                flash.addFlashAttribute(
                        "mensaje",
                        "¡Canje realizado correctamente!"
                );
            } else {
                flash.addFlashAttribute(
                        "error",
                        "El cliente no tiene suficientes puntos."
                );
            }

        } catch (SQLException | IllegalArgumentException e) {
            flash.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/recompensas";
    }
}
 