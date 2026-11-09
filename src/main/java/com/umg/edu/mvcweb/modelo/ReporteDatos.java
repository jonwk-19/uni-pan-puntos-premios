package com.umg.edu.mvcweb.modelo;

import java.util.ArrayList;
import java.util.List;

public class ReporteDatos {

    private String tipo;
    private String titulo;
    private String periodo;
    private List<String> encabezados;
    private List<List<String>> filas;

    public ReporteDatos(String tipo, String titulo, List<String> encabezados) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.encabezados = encabezados;
        this.filas = new ArrayList<>();
    }

    public void agregarFila(List<String> fila) {
        filas.add(fila);
    }

    public String getTipo() {
        return tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public List<String> getEncabezados() {
        return encabezados;
    }

    public List<List<String>> getFilas() {
        return filas;
    }
}
