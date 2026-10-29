package com.panpuntos.modelo;

public class Acompanamiento extends Producto {

    @Override
    public Categoria getCategoria() {
        return Categoria.ACOMPANAMIENTO;
    }

    @Override
    public int getPuntos() {
        return 0;
    }
}
