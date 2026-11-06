
package com.panpuntos.servicio;

import com.panpuntos.dao.PagoDAO;
import com.panpuntos.dao.PedidoDAO;
import com.panpuntos.dao.ProductoDAO;
import com.panpuntos.dao.MenuDAO;
import com.panpuntos.dao.InventarioDAO;

import com.panpuntos.dao.impl.PagoDAOImpl;
import com.panpuntos.dao.impl.PedidoDAOImpl;
import com.panpuntos.dao.impl.ProductoDAOImpl;
import com.panpuntos.dao.impl.MenuDAOImpl;
import com.panpuntos.dao.impl.InventarioDAOImpl;

import com.panpuntos.modelo.Pago;
import com.panpuntos.modelo.Pedido;
import com.panpuntos.modelo.DetallePedido;
import com.panpuntos.modelo.Producto;
import com.panpuntos.modelo.Menu;
import com.panpuntos.modelo.MovimientoInventario;
import com.panpuntos.modelo.TipoMovimientoInventario;
import com.panpuntos.modelo.MetodoPago;
import com.panpuntos.modelo.EstadoPago;
import com.panpuntos.modelo.EstadoPedido;

import com.panpuntos.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PagoService {

    private final PagoDAO pagoDAO = new PagoDAOImpl();
    private final PedidoDAO pedidoDAO = new PedidoDAOImpl();
    private final ProductoDAO productoDAO = new ProductoDAOImpl();
    private final MenuDAO menuDAO = new MenuDAOImpl();
    private final InventarioDAO inventarioDAO = new InventarioDAOImpl();
    private final PuntosService puntosService = new PuntosService();

    public Pago procesarPago(
            Pedido pedido,
            MetodoPago metodo,
            Double montoRecibido) throws SQLException {

        return procesarPago(pedido, metodo, montoRecibido, true);
    }

    public Pago procesarPago(
            Pedido pedido,
            MetodoPago metodo,
            Double montoRecibido,
            boolean tarjetaAprobada) throws SQLException {

        if (pedido == null || metodo == null) {
            throw new IllegalArgumentException(
                    "Pedido y método de pago son obligatorios.");
        }

        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new IllegalArgumentException(
                    "El pedido no tiene productos.");
        }

        try (Connection con = ConexionDB.getConexion()) {

            con.setAutoCommit(false);

            try {

                String sql = "SELECT id_cliente, estado, total "
                        + "FROM pedidos WHERE id_pedido = ? FOR UPDATE";

                try (PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, pedido.getId());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("El pedido no existe.");
                        }

                        if (!EstadoPedido.PENDIENTE.name()
                                .equals(rs.getString("estado"))) {

                            throw new SQLException(
                                    "El pedido no está pendiente de pago.");
                        }

                        int idCliente = rs.getInt("id_cliente");
                        pedido.setIdCliente(
                                rs.wasNull() ? null : idCliente);

                        pedido.setTotal(rs.getDouble("total"));
                        pedido.setEstado(EstadoPedido.PENDIENTE);
                    }
                }

                double total = pedido.getTotal();

                if (metodo == MetodoPago.EFECTIVO) {

                    if (montoRecibido == null || montoRecibido < total) {
                        throw new IllegalArgumentException(
                                "El dinero recibido es insuficiente.");
                    }
                }

                if (metodo == MetodoPago.TARJETA) {
                    montoRecibido = null;
                }

                Pago pago = new Pago();

                pago.setIdPedido(pedido.getId());
                pago.setMetodo(metodo);
                pago.setMonto(total);

                if (metodo == MetodoPago.EFECTIVO) {

                    pago.setEstado(EstadoPago.APROBADO);
                    pago.setMontoRecibido(montoRecibido);
                    pago.setCambio(montoRecibido - total);

                } else {

                    pago.setReferencia(
                            "SIM-" + UUID.randomUUID().toString()
                                    .substring(0, 12).toUpperCase()
                    );

                    pago.setEstado(
                            tarjetaAprobada
                                    ? EstadoPago.APROBADO
                                    : EstadoPago.RECHAZADO
                    );
                }

                if (pago.getEstado() == EstadoPago.RECHAZADO) {

                    pagoDAO.registrar(con, pago);

                    con.commit();

                    pedido.setPuntosGanados(0);

                    return pago;
                }

                for (DetallePedido detalle : pedido.getDetalles()) {

                    if (detalle.getCantidad() <= 0) {
                        throw new SQLException(
                                "La cantidad del producto no es válida.");
                    }

                    if (detalle.getIdProducto() != null) {

                        descontarProducto(
                                con,
                                detalle.getIdProducto(),
                                detalle.getCantidad(),
                                pedido.getId()
                        );

                    } else if (detalle.getIdMenu() != null) {

                        Menu menu = menuDAO.buscarPorId(
                                con,
                                detalle.getIdMenu()
                        );

                        if (menu == null || !menu.isActivo()) {
                            throw new SQLException(
                                    "El menú no está disponible.");
                        }

                        for (Producto componente : menu.getComponentes()) {

                            if (componente == null || !componente.isActivo()) {
                                throw new SQLException(
                                        "Un componente del menú no está disponible.");
                            }

                            descontarProducto(
                                    con,
                                    componente.getId(),
                                    detalle.getCantidad(),
                                    pedido.getId()
                            );
                        }

                    } else {

                        throw new SQLException(
                                "El detalle no tiene producto ni menú.");
                    }
                }

                pagoDAO.registrar(con, pago);

                pedidoDAO.actualizarEstado(
                        con,
                        pedido.getId(),
                        EstadoPedido.PAGADO
                );

                pedido.setEstado(EstadoPedido.PAGADO);

                puntosService.acreditar(con, pedido);

                con.commit();

                return pago;

            } catch (SQLException | RuntimeException e) {

                con.rollback();
                throw e;
            }
        }
    }

    private void descontarProducto(
            Connection con,
            int idProducto,
            int cantidad,
            int idPedido) throws SQLException {

        boolean descontado = productoDAO.descontarExistencia(
                con,
                idProducto,
                cantidad
        );

        if (!descontado) {
            throw new SQLException(
                    "No hay suficiente existencia para el producto ID: "
                    + idProducto);
        }

        MovimientoInventario movimiento = new MovimientoInventario();

        movimiento.setIdProducto(idProducto);
        movimiento.setCantidad(cantidad);
        movimiento.setTipo(TipoMovimientoInventario.SALIDA);
        movimiento.setIdPedido(idPedido);

        inventarioDAO.registrarMovimiento(con, movimiento);
    }
}
