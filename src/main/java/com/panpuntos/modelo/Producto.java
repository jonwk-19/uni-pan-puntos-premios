package com.panpuntos.modelo;

public abstract class Producto {
    private int id;
    private String codigo;
    private String nombre;
    private double precio;
    private int existencia;
    private boolean activo = true;

    public static Producto crear(Categoria categoria) {
        switch (categoria) {
            case SANDWICH:
                return new Sandwich();
            case BEBIDA:
                return new Bebida();
            default:
                return new Acompanamiento();
        }
    }

    public abstract Categoria getCategoria();

    public abstract int getPuntos();

    public boolean estaDisponible(int cantidad) {
        return activo && existencia >= cantidad;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getExistencia() { return existencia; }
    public void setExistencia(int existencia) { this.existencia = existencia; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return getCategoria().getEtiqueta() + " " + codigo + ": " + nombre;
    }
}
