package com.umg.edu.mvcweb.controller;

import com.umg.edu.mvcweb.modelo.ReporteDatos;
import com.umg.edu.mvcweb.servicio.ReporteServicio;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
public class ReporteApiController {

    @Autowired
    private ReporteServicio reporteServicio;

    @GetMapping("/{tipo}")
    public ResponseEntity<?> consultarReporte(
            @PathVariable String tipo) {

        try {

            LocalDate inicio =
                    LocalDate.now().withDayOfYear(1);

            LocalDate fin = LocalDate.now();

            ReporteDatos reporte =
                    reporteServicio.obtenerReporte(tipo, inicio, fin);

            return ResponseEntity.ok(reporte);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error",
                            "Error al consultar el reporte."
                    ));
        }
    }
}
