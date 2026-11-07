package com.umg.edu.mvcweb.controller;

import com.umg.edu.mvcweb.excepcion.NegocioException;
import com.umg.edu.mvcweb.servicio.ProductoServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;

@Controller
public class InventarioController {

    @Autowired
    private ProductoServicio productoServicio;

    @GetMapping("/inventario")
    public String mostrarInventario(
            @RequestParam(required = false) Integer id,
            Model model) throws SQLException {

        cargarPantalla(id, model);

        return "inventario/inventario";
    }

    @PostMapping("/inventario/abastecer")
    public String abastecer(
            @RequestParam int idProducto,
            @RequestParam int cantidad,
            Model model,
            RedirectAttributes flash) throws SQLException {

        try {
            productoServicio.abastecer(idProducto, cantidad);
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
            cargarPantalla(idProducto, model);
            return "inventario/inventario";
        }

        flash.addFlashAttribute("mensaje", "Se registro la entrada de " + cantidad + " unidades");

        return "redirect:/inventario?id=" + idProducto;
    }

    private void cargarPantalla(Integer id, Model model) throws SQLException {

        model.addAttribute("productos", productoServicio.listar());

        if (id == null) {
            model.addAttribute("movimientos", productoServicio.historial());
        } else {
            model.addAttribute("movimientos", productoServicio.historialPorProducto(id));
            model.addAttribute("seleccionado", id);
        }
    }
}
