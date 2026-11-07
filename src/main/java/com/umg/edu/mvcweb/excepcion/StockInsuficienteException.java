package com.umg.edu.mvcweb.excepcion;

public class StockInsuficienteException extends NegocioException {
    private final int disponible;

    public StockInsuficienteException(String producto, int disponible) {
        super("No hay existencia suficiente de " + producto + ". Unidades disponibles: " + disponible);
        this.disponible = disponible;
    }

    public int getDisponible() {
        return disponible;
    }
}
