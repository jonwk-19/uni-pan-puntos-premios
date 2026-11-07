package com.umg.edu.mvcweb.servicio;

import com.umg.edu.mvcweb.dao.ClienteDAO;
import com.umg.edu.mvcweb.dao.impl.ClienteDAOImpl;
import com.umg.edu.mvcweb.excepcion.NegocioException;
import com.umg.edu.mvcweb.modelo.Cliente;

import java.sql.SQLException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ClienteServicio {

    private final ClienteDAO clienteDAO = new ClienteDAOImpl();

    public List<Cliente> listar() throws SQLException {
        return clienteDAO.listar();
    }

    public List<Cliente> listarActivos() throws SQLException {
        return clienteDAO.listar().stream()
                .filter(Cliente::isActivo)
                .toList();
    }

    public List<Cliente> buscarPorNombre(String texto) throws SQLException {
        if (texto == null || texto.isBlank()) {
            return clienteDAO.listar();
        }

        return clienteDAO.buscarPorNombre(texto.trim());
    }

    public Cliente buscarPorId(int id) throws SQLException {
        return clienteDAO.buscarPorId(id);
    }

    public void registrar(Cliente cliente)
            throws SQLException, NegocioException {

        validar(cliente);

        if (clienteDAO.buscarPorDocumento(cliente.getDocumento()) != null) {
            throw new NegocioException(
                    "Ya existe un cliente con ese documento");
        }

        clienteDAO.insertar(cliente);
    }

    public void modificar(Cliente cliente)
            throws SQLException, NegocioException {

        validar(cliente);

        Cliente existente = clienteDAO.buscarPorId(cliente.getId());

        if (existente == null) {
            throw new NegocioException("El cliente no existe");
        }

        Cliente repetido =
                clienteDAO.buscarPorDocumento(cliente.getDocumento());

        if (repetido != null && repetido.getId() != cliente.getId()) {
            throw new NegocioException(
                    "Ese documento ya pertenece a otro cliente");
        }

        clienteDAO.actualizar(cliente);
    }

    public void cambiarEstado(int id, boolean activo)
            throws SQLException, NegocioException {

        Cliente cliente = clienteDAO.buscarPorId(id);

        if (cliente == null) {
            throw new NegocioException("El cliente no existe");
        }

        clienteDAO.cambiarEstado(id, activo);
    }

    private void validar(Cliente cliente) throws NegocioException {

        if (cliente.getDocumento() == null
                || cliente.getDocumento().isBlank()) {
            throw new NegocioException("El documento es obligatorio");
        }

        cliente.setDocumento(cliente.getDocumento().trim());

        if (cliente.getNombre() == null
                || cliente.getNombre().isBlank()) {
            throw new NegocioException("El nombre es obligatorio");
        }

        cliente.setNombre(cliente.getNombre().trim());

        if (cliente.getApellidos() == null
                || cliente.getApellidos().isBlank()) {
            throw new NegocioException("Los apellidos son obligatorios");
        }

        cliente.setApellidos(cliente.getApellidos().trim());

        if (cliente.getTelefono() != null) {
            cliente.setTelefono(cliente.getTelefono().trim());
        }

        if (cliente.getEmail() != null) {
            cliente.setEmail(cliente.getEmail().trim());

            if (!cliente.getEmail().isEmpty()
                    && !cliente.getEmail().matches(
                            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                throw new NegocioException(
                        "El correo electronico no tiene un formato valido");
            }
        }
    }
}
