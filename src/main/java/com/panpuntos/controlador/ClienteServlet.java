package com.panpuntos.controlador;

import com.panpuntos.excepcion.NegocioException;
import com.panpuntos.modelo.Cliente;
import com.panpuntos.servicio.ClienteServicio;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/clientes")
public class ClienteServlet extends ControladorBase {

    private final ClienteServicio servicio = new ClienteServicio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String accion = req.getParameter("accion");

        try {
            if ("nuevo".equals(accion)) {
                mostrarFormulario(req, resp, null);

            } else if ("editar".equals(accion)) {
                int id = Integer.parseInt(req.getParameter("id"));
                Cliente cliente = servicio.buscarPorId(id);

                if (cliente == null) {
                    mostrarError(req, resp, "El cliente no existe");
                    return;
                }

                mostrarFormulario(req, resp, cliente);

            } else {
                listar(req, resp);
            }

        } catch (SQLException e) {
            mostrarError(req, resp,
                    "Error de base de datos: " + e.getMessage());

        } catch (NumberFormatException e) {
            mostrarError(req, resp, "El identificador del cliente no es válido");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String accion = req.getParameter("accion");

        try {
            if ("guardar".equals(accion)) {
                guardar(req, resp);

            } else if ("estado".equals(accion)) {
                int id = Integer.parseInt(req.getParameter("id"));
                boolean activo = Boolean.parseBoolean(
                        req.getParameter("activo"));

                servicio.cambiarEstado(id, activo);

                redirigir(req, resp, "/clientes",
                        "Estado del cliente actualizado");

            } else {
                resp.sendRedirect(req.getContextPath() + "/clientes");
            }

        } catch (SQLException e) {
            mostrarError(req, resp,
                    "Error de base de datos: " + e.getMessage());

        } catch (NegocioException e) {
            mostrarError(req, resp, e.getMessage());

        } catch (NumberFormatException e) {
            mostrarError(req, resp, "Los datos recibidos no son válidos");
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {

        String busqueda = req.getParameter("buscar");

        List<Cliente> clientes;

        if (busqueda == null || busqueda.isBlank()) {
            clientes = servicio.listar();
        } else {
            clientes = servicio.buscarPorNombre(busqueda);
            req.setAttribute("busqueda", busqueda);
        }

        req.setAttribute("clientes", clientes);

        irA(req, resp, "clientes/lista");
    }

    private void mostrarFormulario(HttpServletRequest req,
                                   HttpServletResponse resp,
                                   Cliente cliente)
            throws ServletException, IOException {

        req.setAttribute("cliente", cliente);

        irA(req, resp, "clientes/formulario");
    }

    private void guardar(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {

        String idTexto = req.getParameter("id");

        boolean esNuevo = idTexto == null || idTexto.isBlank();

        Cliente cliente = new Cliente();

        cliente.setDocumento(req.getParameter("documento"));
        cliente.setNombre(req.getParameter("nombre"));
        cliente.setApellidos(req.getParameter("apellidos"));
        cliente.setTelefono(req.getParameter("telefono"));
        cliente.setEmail(req.getParameter("email"));

        try {
            if (esNuevo) {
                servicio.registrar(cliente);
            } else {
                cliente.setId(Integer.parseInt(idTexto));
                servicio.modificar(cliente);
            }

            redirigir(req, resp, "/clientes",
                    esNuevo ? "Cliente registrado correctamente"
                            : "Cliente actualizado correctamente");

        } catch (NegocioException e) {
            req.setAttribute("error", e.getMessage());
            mostrarFormulario(req, resp, cliente);

        } catch (NumberFormatException e) {
            req.setAttribute("error", "El identificador no es válido");
            mostrarFormulario(req, resp, cliente);
        }
    }
} 