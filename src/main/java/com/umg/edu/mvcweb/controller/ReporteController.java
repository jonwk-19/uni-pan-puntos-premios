package com.umg.edu.mvcweb.controller;

import com.umg.edu.mvcweb.servicio.ReporteServicio;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;

@Controller
public class ReporteController {

    @Autowired
    private ReporteServicio reporteServicio;

    @GetMapping("/reportes")
    public String mostrarReportes(Model model) {

        model.addAttribute(
                "inicio",
                LocalDate.now().withDayOfYear(1).toString()
        );

        model.addAttribute("fin", LocalDate.now().toString());

        return "reportes/lista";
    }

    @GetMapping("/reportes/pdf/{tipo}")
    public ResponseEntity<byte[]> descargarReporte(
            @PathVariable String tipo,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fin) {

        try {

            if (inicio == null) {
                inicio = LocalDate.now().withDayOfYear(1);
            }

            if (fin == null) {
                fin = LocalDate.now();
            }

            byte[] pdf = reporteServicio.generarPdf(tipo, inicio, fin);

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_PDF);

            headers.set(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"reporte_" + tipo + ".pdf\""
            );

            return new ResponseEntity<>(
                    pdf,
                    headers,
                    HttpStatus.OK
            );

        } catch (IllegalArgumentException e) {

            return respuestaError(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {

            return respuestaError(
                    "Error al generar el reporte: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private ResponseEntity<byte[]> respuestaError(
            String mensaje, HttpStatus estado) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        return new ResponseEntity<>(
                mensaje.getBytes(StandardCharsets.UTF_8),
                headers,
                estado
        );
    }
} 
