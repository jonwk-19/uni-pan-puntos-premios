package com.panpuntos.modelo;

public enum Categoria {
    SANDWICH("Sandwich"),
    BEBIDA("Bebida"),
    ACOMPANAMIENTO("Acompanamiento");

    private final String etiqueta;

    Categoria(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() { return etiqueta; }
}
