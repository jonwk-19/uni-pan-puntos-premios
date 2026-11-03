
package com.panpuntos.servicio;

import com.panpuntos.dao.ClienteDAO;
import com.panpuntos.dao.PuntosDAO;
import com.panpuntos.dao.impl.ClienteDAOImpl;
import com.panpuntos.dao.impl.PuntosDAOImpl;
import com.panpuntos.modelo.MovimientoPuntos;
import com.panpuntos.modelo.Pedido;
import com.panpuntos.modelo.TipoMovimientoPuntos;

import java.sql.Connection;
import java.sql.SQLException;

public class PuntosService {

    private final ClienteDAO clienteDAO = new ClienteDAOImpl();
    private final PuntosDAO puntosDAO = new PuntosDAOImpl();

    public int calcularPuntos(Pedido pedido) {
        return (int) Math.floor(pedido.getTotal() / 10);
    }

    public void acreditar(Connection con, Pedido pedido)
            throws SQLException {

        if (pedido.getIdCliente() == null) {
            return;
        }

        int puntos = calcularPuntos(pedido);

        if (puntos <= 0) {
            return;
        }

        clienteDAO.sumarPuntos(
                con,
                pedido.getIdCliente(),
                puntos
        );

        MovimientoPuntos movimiento = new MovimientoPuntos();
        movimiento.setIdCliente(pedido.getIdCliente());
        movimiento.setPuntos(puntos);
        movimiento.setTipo(TipoMovimientoPuntos.ACUMULACION);
        movimiento.setIdPedido(pedido.getId());
        movimiento.setIdRecompensa(null);

        puntosDAO.registrarMovimiento(con, movimiento);

        pedido.setPuntosGanados(puntos);
    }
}
