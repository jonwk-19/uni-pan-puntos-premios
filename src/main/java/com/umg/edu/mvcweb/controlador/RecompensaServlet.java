
package com.umg.edu.mvcweb.controlador;

import com.umg.edu.mvcweb.dao.impl.ClienteDAOImpl;
import com.umg.edu.mvcweb.dao.impl.RecompensaDAOImpl;
import com.umg.edu.mvcweb.modelo.Cliente;
import com.umg.edu.mvcweb.modelo.Recompensa;
import com.umg.edu.mvcweb.servicio.RecompensaServicio;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/recompensas")
public class RecompensaServlet extends HttpServlet {

    private final ClienteDAOImpl clienteDAO = new ClienteDAOImpl();
    private final RecompensaDAOImpl recompensaDAO = new RecompensaDAOImpl();
    private final RecompensaServicio recompensaServicio =
            new RecompensaServicio();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        cargarCatalogo(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {
            int idCliente = Integer.parseInt(
                    request.getParameter("idCliente")
            );

            int idRecompensa = Integer.parseInt(
                    request.getParameter("idRecompensa")
            );

            boolean realizado = recompensaServicio.canjear(
                    idCliente, idRecompensa
            );

            if (realizado) {
                request.setAttribute(
                        "mensaje",
                        "Canje realizado correctamente."
                );
            } else {
                request.setAttribute(
                        "error",
                        "El cliente no tiene suficientes puntos."
                );
            }

            request.setAttribute("idClienteSeleccionado", idCliente);

        } catch (NumberFormatException e) {

            request.setAttribute(
                    "error",
                    "Selecciona un cliente y una recompensa válidos."
            );

        } catch (SQLException e) {

            request.setAttribute(
                    "error",
                    "No se pudo realizar el canje: " + e.getMessage()
            );
        }

        cargarCatalogo(request, response);
    }

    private void cargarCatalogo(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            List<Cliente> clientes = clienteDAO.listar();
            List<Recompensa> recompensas =
                    recompensaDAO.listarActivas();

            request.setAttribute("clientes", clientes);
            request.setAttribute("recompensas", recompensas);

            request.getRequestDispatcher(
                    "/WEB-INF/vista/recompensas/catalogo.jsp"
            ).forward(request, response);

        } catch (SQLException e) {

            throw new ServletException(
                    "Error al cargar el catálogo de recompensas.", e
            );
        }
    }
}
