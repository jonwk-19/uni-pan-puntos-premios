package com.panpuntos.servicio;

import com.panpuntos.dao.ClienteDAO;
import com.panpuntos.dao.impl.ClienteDAOImpl;
import com.panpuntos.modelo.Cliente;

import java.sql.SQLException;
import java.util.List;

public class ClienteService {

    private final ClienteDAO clienteDAO = new ClienteDAOImpl();

    public List<Cliente> listarActivos() throws SQLException {
        List<Cliente> clientes = clienteDAO.listar();

        return clientes.stream()
                .filter(Cliente::isActivo)
                .toList();
    }

    public Cliente buscarPorId(int id) throws SQLException {
        return clienteDAO.buscarPorId(id);
    }
}