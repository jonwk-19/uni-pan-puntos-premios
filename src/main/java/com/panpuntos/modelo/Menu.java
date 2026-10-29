package com.panpuntos.modelo;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private int id;
    private String codigo;
    private String nombre;
    private double precio;
    private Sandwich sandwich;
    private Bebida bebida;
    private Acompanamiento acompanamiento;
    private boolean activo = true;

    public List<Producto> getComponentes() {
        List<Producto> componentes = new ArrayList<>();
        componentes.add(sandwich);
        componentes.add(bebida);
        componentes.add(acompanamiento);
        return componentes;
    }

    public boolean estaDisponible(int cantidad) {
        if (!activo) {
            return false;
        }
        for (Producto p : getComponentes()) {
            if (!p.estaDisponible(cantidad)) {
                return false;
            }
        }
        return true;
    }

    public int getPuntos() {
        return 8;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public Sandwich getSandwich() { return sandwich; }
    public void setSandwich(Sandwich sandwich) { this.sandwich = sandwich; }

    public Bebida getBebida() { return bebida; }
    public void setBebida(Bebida bebida) { this.bebida = bebida; }

    public Acompanamiento getAcompanamiento() { return acompanamiento; }
    public void setAcompanamiento(Acompanamiento acompanamiento) { this.acompanamiento = acompanamiento; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
