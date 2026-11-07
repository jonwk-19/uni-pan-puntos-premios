package com.umg.edu.mvcweb.controller;

import com.umg.edu.mvcweb.excepcion.NegocioException;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

@ControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(NegocioException.class)
    public String negocio(NegocioException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String argumentoInvalido(IllegalArgumentException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error";
    }

    @ExceptionHandler(SQLException.class)
    public String baseDeDatos(SQLException e, Model model) {
        model.addAttribute("error", "Error de base de datos: " + e.getMessage());
        return "error";
    }
}
