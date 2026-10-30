package com.panpuntos.controlador;

import com.panpuntos.excepcion.NegocioException;
import com.panpuntos.servicio.ProductoServicio;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/inventario")
public class InventarioServlet extends ControladorBase {
    private final ProductoServicio servicio = new ProductoServicio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        mostrar(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int idProducto = Integer.parseInt(req.getParameter("idProducto"));
            int cantidad = Integer.parseInt(req.getParameter("cantidad"));
            servicio.abastecer(idProducto, cantidad);
            redirigir(req, resp, "/inventario?id=" + idProducto, "Se registro la entrada de " + cantidad + " unidades");
        } catch (NegocioException e) {
            req.setAttribute("error", e.getMessage());
            mostrar(req, resp);
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Selecciona un producto y escribe una cantidad numerica");
            mostrar(req, resp);
        } catch (SQLException e) {
            mostrarError(req, resp, "Error de base de datos: " + e.getMessage());
        }
    }

    private void mostrar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String idTexto = req.getParameter("id");
            if (idTexto == null || idTexto.isEmpty()) {
                req.setAttribute("movimientos", servicio.historial());
            } else {
                int id = Integer.parseInt(idTexto);
                req.setAttribute("movimientos", servicio.historialPorProducto(id));
                req.setAttribute("seleccionado", id);
            }
            req.setAttribute("productos", servicio.listar());
            irA(req, resp, "inventario/inventario");
        } catch (SQLException e) {
            mostrarError(req, resp, "Error de base de datos: " + e.getMessage());
        } catch (NumberFormatException e) {
            mostrarError(req, resp, "Identificador de producto invalido");
        }
    }
}
