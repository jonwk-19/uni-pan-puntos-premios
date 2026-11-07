package com.umg.edu.mvcweb.modelo;

public class Sandwich extends Producto {

    @Override
    public Categoria getCategoria() {
        return Categoria.SANDWICH;
    }

    @Override
    public int getPuntos() {
        return 2;
    }
}
