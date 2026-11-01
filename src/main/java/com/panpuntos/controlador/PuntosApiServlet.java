package com.panpuntos.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.panpuntos.util.ConexionDB;

@WebServlet("/api/puntos")
public class PuntosApiServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*"); 
        
        PrintWriter out = resp.getWriter();
        String documento = req.getParameter("documento");

        if (documento == null || documento.trim().isEmpty()) {
            resp.setStatus(400);
            out.print("{\"error\": \"Falta el parametro documento\"}");
            return;
        }

        String sql = "SELECT documento, CONCAT(nombre, ' ', apellidos) AS nombre_completo, puntos FROM clientes WHERE documento = ? AND activo = TRUE";
        
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String json = String.format(
                        "{\"documento\":\"%s\", \"nombre\":\"%s\", \"puntos\":%d}",
                        rs.getString("documento"), rs.getString("nombre_completo"), rs.getInt("puntos")
                    );
                    out.print(json);
                } else {
                    resp.setStatus(404);
                    out.print("{\"error\": \"Cliente no encontrado o inactivo\"}");
                }
            }
        } catch (Exception e) {
            resp.setStatus(500);
            out.print("{\"error\": \"Error en el servidor: " + e.getMessage() + "\"}");
        }
    }
}