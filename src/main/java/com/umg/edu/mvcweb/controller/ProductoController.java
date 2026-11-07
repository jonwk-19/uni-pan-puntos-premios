package com.umg.edu.mvcweb.controller;

import com.umg.edu.mvcweb.excepcion.NegocioException;
import com.umg.edu.mvcweb.form.ProductoForm;
import com.umg.edu.mvcweb.modelo.Categoria;
import com.umg.edu.mvcweb.modelo.Producto;
import com.umg.edu.mvcweb.servicio.ProductoServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.List;

@Controller
public class ProductoController {

    @Autowired
    private ProductoServicio productoServicio;

    @GetMapping("/productos")
    public String mostrarProductos(
            @RequestParam(required = false) Categoria categoria,
            Model model) throws SQLException {

        List<Producto> productos = categoria == null
                ? productoServicio.listar()
                : productoServicio.listarPorCategoria(categoria);

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", Categoria.values());
        model.addAttribute("filtro", categoria);

        return "productos/lista";
    }

    @GetMapping("/productos/nuevo")
    public String nuevoProducto(Model model) {

        model.addAttribute("productoForm", new ProductoForm());

        return prepararFormulario(model, false);
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(
            @ModelAttribute ProductoForm productoForm,
            BindingResult resultado,
            Model model,
            RedirectAttributes flash) throws SQLException {

        if (resultado.hasErrors()) {
            model.addAttribute("error", "El precio y la existencia deben ser numeros validos");
            return prepararFormulario(model, false);
        }

        try {
            productoServicio.registrar(productoForm.aProducto());
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
            return prepararFormulario(model, false);
        }

        flash.addFlashAttribute("mensaje", "Producto registrado");

        return "redirect:/productos";
    }

    @GetMapping("/productos/editar/{id}")
    public String editarProducto(
            @PathVariable int id,
            Model model) throws SQLException, NegocioException {

        Producto producto = productoServicio.buscarPorId(id);

        if (producto == null) {
            throw new NegocioException("El producto no existe");
        }

        model.addAttribute("productoForm", ProductoForm.desde(producto));

        return prepararFormulario(model, true);
    }

    @PostMapping("/productos/actualizar")
    public String actualizarProducto(
            @ModelAttribute ProductoForm productoForm,
            BindingResult resultado,
            Model model,
            RedirectAttributes flash) throws SQLException {

        if (resultado.hasErrors()) {
            model.addAttribute("error", "El precio debe ser un numero valido");
            return prepararFormulario(model, true);
        }

        try {
            productoServicio.modificar(productoForm.aProducto());
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
            return prepararFormulario(model, true);
        }

        flash.addFlashAttribute("mensaje", "Producto actualizado");

        return "redirect:/productos";
    }

    @PostMapping("/productos/estado/{id}")
    public String cambiarEstado(
            @PathVariable int id,
            @RequestParam boolean activo,
            RedirectAttributes flash) throws SQLException, NegocioException {

        productoServicio.cambiarEstado(id, activo);

        flash.addFlashAttribute("mensaje", "Estado del producto actualizado");

        return "redirect:/productos";
    }

    private String prepararFormulario(Model model, boolean editando) {

        model.addAttribute("categorias", Categoria.values());
        model.addAttribute("editando", editando);

        return "productos/formulario";
    }
}
