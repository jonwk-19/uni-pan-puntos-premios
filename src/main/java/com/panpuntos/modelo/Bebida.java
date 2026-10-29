package com.panpuntos.modelo;

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
