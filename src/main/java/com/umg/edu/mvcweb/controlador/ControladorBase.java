package com.umg.edu.mvcweb.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class ControladorBase extends HttpServlet {

    protected void irA(HttpServletRequest req, HttpServletResponse resp, String vista)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/vista/" + vista + ".jsp").forward(req, resp);
    }

    protected void mostrarError(HttpServletRequest req, HttpServletResponse resp, String mensaje)
            throws ServletException, IOException {
        req.setAttribute("error", mensaje);
        irA(req, resp, "comun/error");
    }

    protected void redirigir(HttpServletRequest req, HttpServletResponse resp, String ruta, String mensaje)
            throws IOException {
        req.getSession().setAttribute("mensaje", mensaje);
        resp.sendRedirect(req.getContextPath() + ruta);
    }
}
