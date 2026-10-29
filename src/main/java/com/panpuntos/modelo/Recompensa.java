package com.panpuntos.modelo;

public class Recompensa {
    private int id;
    private String nombre;
    private int puntosRequeridos;
    private Integer idProducto;
    private Integer idMenu;
    private boolean activa = true;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getPuntosRequeridos() { return puntosRequeridos; }
    public void setPuntosRequeridos(int puntosRequeridos) { this.puntosRequeridos = puntosRequeridos; }

    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

    public Integer getIdMenu() { return idMenu; }
    public void setIdMenu(Integer idMenu) { this.idMenu = idMenu; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
