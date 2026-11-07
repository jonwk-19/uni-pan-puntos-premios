package com.umg.edu.mvcweb.controlador;

import com.umg.edu.mvcweb.excepcion.NegocioException;
import com.umg.edu.mvcweb.modelo.Categoria;
import com.umg.edu.mvcweb.modelo.Producto;
import com.umg.edu.mvcweb.servicio.ProductoServicio;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/productos")
public class ProductoServlet extends ControladorBase {
    private final ProductoServicio servicio = new ProductoServicio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String accion = req.getParameter("accion");
        try {
            if ("nuevo".equals(accion)) {
                mostrarFormulario(req, resp, null);
            } else if ("editar".equals(accion)) {
                Producto producto = servicio.buscarPorId(Integer.parseInt(req.getParameter("id")));
                if (producto == null) {
                    mostrarError(req, resp, "El producto no existe");
                    return;
                }
                mostrarFormulario(req, resp, producto);
            } else {
                listar(req, resp);
            }
        } catch (SQLException e) {
            mostrarError(req, resp, "Error de base de datos: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            mostrarError(req, resp, "Los datos recibidos no son validos");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String accion = req.getParameter("accion");
        try {
            if ("guardar".equals(accion)) {
                guardar(req, resp);
            } else if ("estado".equals(accion)) {
                servicio.cambiarEstado(Integer.parseInt(req.getParameter("id")),
                        Boolean.parseBoolean(req.getParameter("activo")));
                redirigir(req, resp, "/productos", "Estado del producto actualizado");
            } else {
                resp.sendRedirect(req.getContextPath() + "/productos");
            }
        } catch (SQLException e) {
            mostrarError(req, resp, "Error de base de datos: " + e.getMessage());
        } catch (NegocioException e) {
            mostrarError(req, resp, e.getMessage());
        } catch (IllegalArgumentException e) {
            mostrarError(req, resp, "Los datos recibidos no son validos");
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        String filtro = req.getParameter("categoria");
        List<Producto> productos;
        if (filtro == null || filtro.isEmpty()) {
            productos = servicio.listar();
        } else {
            productos = servicio.listarPorCategoria(Categoria.valueOf(filtro));
            req.setAttribute("filtro", filtro);
        }
        req.setAttribute("productos", productos);
        req.setAttribute("categorias", Categoria.values());
        irA(req, resp, "productos/lista");
    }

    private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp, Producto producto)
            throws ServletException, IOException {
        req.setAttribute("producto", producto);
        req.setAttribute("categorias", Categoria.values());
        irA(req, resp, "productos/formulario");
    }

    private void guardar(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        String idTexto = req.getParameter("id");
        boolean esNuevo = idTexto == null || idTexto.isEmpty();

        Producto producto = Producto.crear(Categoria.valueOf(req.getParameter("categoria")));
        producto.setCodigo(req.getParameter("codigo"));
        producto.setNombre(req.getParameter("nombre"));
        try {
            if (!esNuevo) {
                producto.setId(Integer.parseInt(idTexto));
            }
            producto.setPrecio(Double.parseDouble(req.getParameter("precio").replace(',', '.')));
            if (esNuevo) {
                producto.setExistencia(Integer.parseInt(req.getParameter("existencia")));
                servicio.registrar(producto);
            } else {
                servicio.modificar(producto);
            }
            redirigir(req, resp, "/productos", esNuevo ? "Producto registrado" : "Producto actualizado");
        } catch (NegocioException e) {
            req.setAttribute("error", e.getMessage());
            mostrarFormulario(req, resp, producto);
        } catch (NumberFormatException e) {
            req.setAttribute("error", "El precio y la existencia deben ser numeros validos");
            mostrarFormulario(req, resp, producto);
        }
    }
}
