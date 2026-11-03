
package com.panpuntos.controlador;

import com.panpuntos.dao.impl.PuntosDAOImpl;
import com.panpuntos.modelo.MovimientoPuntos;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/historial-puntos")
public class HistorialPuntosServlet extends ControladorBase {

    private final PuntosDAOImpl puntosDAO = new PuntosDAOImpl();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            String idTexto = request.getParameter("idCliente");

            if (idTexto == null || idTexto.trim().isEmpty()) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Debe indicar el cliente."
                );
                return;
            }

            int idCliente;

            try {
                idCliente = Integer.parseInt(idTexto);
            } catch (NumberFormatException e) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "El ID del cliente no es válido."
                );
                return;
            }

            if (idCliente <= 0) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "El ID del cliente debe ser positivo."
                );
                return;
            }

            List<MovimientoPuntos> movimientos =
                    puntosDAO.historialPorCliente(idCliente);

            request.setAttribute("movimientos", movimientos);
            request.setAttribute("idCliente", idCliente);

            request.getRequestDispatcher(
                    "/WEB-INF/vista/clientes/historial.jsp"
            ).forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Error al consultar el historial de puntos.", e
            );
        }
    }
}
