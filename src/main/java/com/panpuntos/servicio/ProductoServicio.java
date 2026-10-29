package com.panpuntos.servicio;

import com.panpuntos.dao.ProductoDAO;
import com.panpuntos.dao.impl.ProductoDAOImpl;
import com.panpuntos.excepcion.NegocioException;
import com.panpuntos.modelo.Categoria;
import com.panpuntos.modelo.Producto;

import java.sql.SQLException;
import java.util.List;

public class ProductoServicio {
    private final ProductoDAO productoDAO = new ProductoDAOImpl();

    public List<Producto> listar() throws SQLException {
        return productoDAO.listar();
    }

    public List<Producto> listarPorCategoria(Categoria categoria) throws SQLException {
        return productoDAO.listarPorCategoria(categoria);
    }

    public List<Producto> listarBajoStock(int limite) throws SQLException {
        return productoDAO.listarBajoStock(limite);
    }

    public Producto buscarPorId(int id) throws SQLException {
        return productoDAO.buscarPorId(id);
    }

    public void registrar(Producto producto) throws SQLException, NegocioException {
        validar(producto);
        if (productoDAO.buscarPorCodigo(producto.getCodigo()) != null) {
            throw new NegocioException("Ya existe un producto con el codigo " + producto.getCodigo());
        }
        productoDAO.insertar(producto);
    }

    public void modificar(Producto producto) throws SQLException, NegocioException {
        validar(producto);
        if (productoDAO.buscarPorId(producto.getId()) == null) {
            throw new NegocioException("El producto no existe");
        }
        Producto repetido = productoDAO.buscarPorCodigo(producto.getCodigo());
        if (repetido != null && repetido.getId() != producto.getId()) {
            throw new NegocioException("Ya existe un producto con el codigo " + producto.getCodigo());
        }
        productoDAO.actualizar(producto);
    }

    public void cambiarEstado(int id, boolean activo) throws SQLException, NegocioException {
        if (productoDAO.buscarPorId(id) == null) {
            throw new NegocioException("El producto no existe");
        }
        productoDAO.cambiarEstado(id, activo);
    }

    private void validar(Producto producto) throws NegocioException {
        if (producto.getCodigo() == null || producto.getCodigo().isBlank()) {
            throw new NegocioException("El codigo es obligatorio");
        }
        producto.setCodigo(producto.getCodigo().trim().toUpperCase());
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new NegocioException("El nombre es obligatorio");
        }
        producto.setNombre(producto.getNombre().trim());
        if (producto.getPrecio() < 0) {
            throw new NegocioException("El precio no puede ser negativo");
        }
        if (producto.getExistencia() < 0) {
            throw new NegocioException("La existencia no puede ser negativa");
        }
    }
}
