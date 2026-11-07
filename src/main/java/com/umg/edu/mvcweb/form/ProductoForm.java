package com.umg.edu.mvcweb.form;

import com.umg.edu.mvcweb.modelo.Categoria;
import com.umg.edu.mvcweb.modelo.Producto;

public class ProductoForm {
    private int id;
    private String codigo;
    private Categoria categoria = Categoria.SANDWICH;
    private String nombre;
    private double precio;
    private int existencia;

    public static ProductoForm desde(Producto producto) {
        ProductoForm form = new ProductoForm();
        form.setId(producto.getId());
        form.setCodigo(producto.getCodigo());
        form.setCategoria(producto.getCategoria());
        form.setNombre(producto.getNombre());
        form.setPrecio(producto.getPrecio());
        form.setExistencia(producto.getExistencia());
        return form;
    }

    public Producto aProducto() {
        Producto producto = Producto.crear(categoria);
        producto.setId(id);
        producto.setCodigo(codigo);
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setExistencia(existencia);
        return producto;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getExistencia() { return existencia; }
    public void setExistencia(int existencia) { this.existencia = existencia; }
}
