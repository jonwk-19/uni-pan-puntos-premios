package com.umg.edu.mvcweb.servicio;

import com.umg.edu.mvcweb.dao.InventarioDAO;
import com.umg.edu.mvcweb.dao.ProductoDAO;
import com.umg.edu.mvcweb.dao.impl.InventarioDAOImpl;
import com.umg.edu.mvcweb.dao.impl.ProductoDAOImpl;
import com.umg.edu.mvcweb.excepcion.NegocioException;
import com.umg.edu.mvcweb.excepcion.StockInsuficienteException;
import com.umg.edu.mvcweb.modelo.Categoria;
import com.umg.edu.mvcweb.modelo.MovimientoInventario;
import com.umg.edu.mvcweb.modelo.Producto;
import com.umg.edu.mvcweb.modelo.TipoMovimientoInventario;
import com.umg.edu.mvcweb.util.ConexionDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductoServicio {
    private final ProductoDAO productoDAO = new ProductoDAOImpl();
    private final InventarioDAO inventarioDAO = new InventarioDAOImpl();

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
        int existenciaInicial = producto.getExistencia();
        producto.setExistencia(0);
        productoDAO.insertar(producto);
        if (existenciaInicial > 0) {
            abastecer(producto.getId(), existenciaInicial);
            producto.setExistencia(existenciaInicial);
        }
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

    public void abastecer(int idProducto, int cantidad) throws SQLException, NegocioException {
        if (cantidad <= 0) {
            throw new NegocioException("La cantidad debe ser mayor a cero");
        }
        if (productoDAO.buscarPorId(idProducto) == null) {
            throw new NegocioException("El producto no existe");
        }

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setIdProducto(idProducto);
        movimiento.setCantidad(cantidad);
        movimiento.setTipo(TipoMovimientoInventario.ENTRADA);

        try (Connection con = ConexionDB.getConexion()) {
            con.setAutoCommit(false);
            try {
                productoDAO.aumentarExistencia(con, idProducto, cantidad);
                inventarioDAO.registrarMovimiento(con, movimiento);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void validarVenta(Producto producto, int cantidad) throws NegocioException {
        if (!producto.isActivo()) {
            throw new NegocioException("El producto " + producto.getNombre() + " esta desactivado y no se puede vender");
        }
        if (producto.getExistencia() < cantidad) {
            throw new StockInsuficienteException(producto.getNombre(), producto.getExistencia());
        }
    }

    public List<MovimientoInventario> historial() throws SQLException {
        return inventarioDAO.historial();
    }

    public List<MovimientoInventario> historialPorProducto(int idProducto) throws SQLException {
        return inventarioDAO.historialPorProducto(idProducto);
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
