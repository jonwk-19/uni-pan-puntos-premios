package com.umg.edu.mvcweb.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MovimientoPuntos {
    private int id;
    private int idCliente;
    private LocalDateTime fecha;
    private int puntos;
    private TipoMovimientoPuntos tipo;
    private Integer idPedido;
    private Integer idRecompensa;

    public String getFechaTexto() {
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }

    public TipoMovimientoPuntos getTipo() { return tipo; }
    public void setTipo(TipoMovimientoPuntos tipo) { this.tipo = tipo; }

    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }

    public Integer getIdRecompensa() { return idRecompensa; }
    public void setIdRecompensa(Integer idRecompensa) { this.idRecompensa = idRecompensa; }
}
