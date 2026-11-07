package com.umg.edu.mvcweb.modelo;

public class Bebida extends Producto {

    @Override
    public Categoria getCategoria() {
        return Categoria.BEBIDA;
    }

    @Override
    public int getPuntos() {
        return 0;
    }
}
