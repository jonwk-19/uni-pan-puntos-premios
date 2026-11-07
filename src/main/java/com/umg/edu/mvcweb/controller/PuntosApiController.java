package com.umg.edu.mvcweb.controller;

import com.umg.edu.mvcweb.dao.ClienteDAO;
import com.umg.edu.mvcweb.modelo.Cliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class PuntosApiController {

    @Autowired
    private ClienteDAO clienteDAO;

    @GetMapping("/api/puntos")
    public ResponseEntity<Map<String, Object>> consultarPuntos(
            @RequestParam(required = false) String documento) {

        if (documento == null || documento.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Falta el parametro documento"));
        }

        try {
            Cliente cliente = clienteDAO.buscarPorDocumento(documento.trim());

            if (cliente == null || !cliente.isActivo()) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Cliente no encontrado o inactivo"));
            }

            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("documento", cliente.getDocumento());
            cuerpo.put("nombre", cliente.getNombreCompleto());
            cuerpo.put("puntos", cliente.getPuntos());

            return ResponseEntity.ok(cuerpo);

        } catch (SQLException e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error en el servidor: " + e.getMessage()));
        }
    }
}
