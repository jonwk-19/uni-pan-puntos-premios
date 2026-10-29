package com.panpuntos.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private int id;
    private Integer idCliente;
    private LocalDateTime fecha;
    private EstadoPedido estado = EstadoPedido.PENDIENTE;
    private double total;
    private int puntosGanados;
    private List<DetallePedido> detalles = new ArrayList<>();

    public void agregarDetalle(DetallePedido detalle) {
        detalles.add(detalle);
    }

    public double calcularTotal() {
        double suma = 0;
        for (DetallePedido d : detalles) {
            suma += d.getSubtotal();
        }
        return suma;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public int getPuntosGanados() { return puntosGanados; }
    public void setPuntosGanados(int puntosGanados) { this.puntosGanados = puntosGanados; }

    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }
}
