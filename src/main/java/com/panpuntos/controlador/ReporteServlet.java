package com.panpuntos.controlador;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.panpuntos.util.ConexionDB;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/reportes")
public class ReporteServlet extends ControladorBase {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tipo = req.getParameter("tipo");
        
        if (tipo == null || tipo.isEmpty()) {
            // Si no hay tipo, mostramos la vista JSP con los botones de reportes
            irA(req, resp, "reportes/lista");
            return;
        }

        try {
            generarPdf(req, resp, tipo);
        } catch (SQLException e) {
            mostrarError(req, resp, "Error de base de datos al generar reporte: " + e.getMessage());
        } catch (Exception e) {
            mostrarError(req, resp, "Error al generar PDF: " + e.getMessage());
        }
    }

    private void generarPdf(HttpServletRequest req, HttpServletResponse resp, String tipo) throws SQLException, IOException {
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=reporte_" + tipo + ".pdf");

        try (Connection con = ConexionDB.getConexion();
             PdfWriter writer = new PdfWriter(resp.getOutputStream());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph("Pan, Puntos y Premios")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));
            
            String titulo = obtenerTitulo(tipo);
            document.add(new Paragraph(titulo).setFontSize(14).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            switch (tipo) {
                case "ventas_periodo":
                    reporteVentasPeriodo(con, document, req);
                    break;
                case "productos_vendidos":
                    reporteProductosVendidos(con, document);
                    break;
                case "bajo_stock":
                    reporteBajoStock(con, document);
                    break;
                case "puntos_cliente":
                    reportePuntosCliente(con, document);
                    break;
                case "canjes":
                    reporteCanjes(con, document);
                    break;
                case "menus_vendidos":
                    reporteMenusVendidos(con, document);
                    break;
                default:
                    document.add(new Paragraph("Tipo de reporte no válido."));
            }
        }
    }

    private String obtenerTitulo(String tipo) {
        switch (tipo) {
            case "ventas_periodo": return "Reporte: Ventas por Período (Efectivo vs Tarjeta)";
            case "productos_vendidos": return "Reporte: Productos Más Vendidos";
            case "bajo_stock": return "Reporte: Productos Agotados o con Existencia <= 5";
            case "puntos_cliente": return "Reporte: Puntos Acumulados por Cliente";
            case "canjes": return "Reporte: Historial de Canjes";
            case "menus_vendidos": return "Reporte: Ventas de Menús Completos";
            default: return "Reporte";
        }
    }

    private void reporteVentasPeriodo(Connection con, Document doc, HttpServletRequest req) throws SQLException {
        String inicio = req.getParameter("inicio") != null ? req.getParameter("inicio") : "2026-01-01";
        String fin = req.getParameter("fin") != null ? req.getParameter("fin") : "2026-12-31";

        String sql = "SELECT DATE(p.fecha) AS fecha, " +
                     "SUM(CASE WHEN pg.metodo = 'EFECTIVO' THEN pg.monto ELSE 0 END) AS efectivo, " +
                     "SUM(CASE WHEN pg.metodo = 'TARJETA' THEN pg.monto ELSE 0 END) AS tarjeta " +
                     "FROM pedidos p JOIN pagos pg ON p.id_pedido = pg.id_pedido " +
                     "WHERE p.estado IN ('PAGADO', 'ENTREGADO') AND pg.estado = 'APROBADO' " +
                     "AND DATE(p.fecha) BETWEEN ? AND ? GROUP BY DATE(p.fecha)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, inicio);
            ps.setString(2, fin);
            try (ResultSet rs = ps.executeQuery()) {
                Table table = new Table(new float[]{2, 2, 2});
                table.setWidth(500);
                table.addCell(cabecera("Fecha"));
                table.addCell(cabecera("Efectivo ($)"));
                table.addCell(cabecera("Tarjeta ($)"));
                
                while (rs.next()) {
                    table.addCell(rs.getString("fecha"));
                    table.addCell(String.format("%.2f", rs.getDouble("efectivo")));
                    table.addCell(String.format("%.2f", rs.getDouble("tarjeta")));
                }
                doc.add(new Paragraph("Período: " + inicio + " a " + fin));
                doc.add(table);
            }
        }
    }

    private void reporteProductosVendidos(Connection con, Document doc) throws SQLException {
        String sql = "SELECT pr.nombre, pr.categoria, SUM(dp.cantidad) AS total_vendido " +
                     "FROM detalle_pedido dp JOIN productos pr ON dp.id_producto = pr.id_producto " +
                     "JOIN pedidos p ON dp.id_pedido = p.id_pedido " +
                     "WHERE p.estado IN ('PAGADO', 'ENTREGADO') " +
                     "GROUP BY pr.id_producto, pr.nombre, pr.categoria ORDER BY total_vendido DESC";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            Table table = new Table(new float[]{3, 2, 2});
            table.setWidth(500);
            table.addCell(cabecera("Producto"));
            table.addCell(cabecera("Categoría"));
            table.addCell(cabecera("Cantidad Vendida"));
            while (rs.next()) {
                table.addCell(rs.getString("nombre"));
                table.addCell(rs.getString("categoria"));
                table.addCell(String.valueOf(rs.getInt("total_vendido")));
            }
            doc.add(table);
        }
    }

    private void reporteBajoStock(Connection con, Document doc) throws SQLException {
        String sql = "SELECT codigo, nombre, categoria, existencia FROM productos " +
                     "WHERE activo = TRUE AND existencia <= 5 ORDER BY existencia ASC";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            Table table = new Table(new float[]{2, 3, 2, 2});
            table.setWidth(500);
            table.addCell(cabecera("Código"));
            table.addCell(cabecera("Nombre"));
            table.addCell(cabecera("Categoría"));
            table.addCell(cabecera("Existencia"));
            while (rs.next()) {
                table.addCell(rs.getString("codigo"));
                table.addCell(rs.getString("nombre"));
                table.addCell(rs.getString("categoria"));
                table.addCell(String.valueOf(rs.getInt("existencia")));
            }
            doc.add(table);
        }
    }

    private void reportePuntosCliente(Connection con, Document doc) throws SQLException {
        String sql = "SELECT c.documento, CONCAT(c.nombre, ' ', c.apellidos) AS cliente, c.puntos " +
                     "FROM clientes c WHERE c.activo = TRUE ORDER BY c.puntos DESC";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            Table table = new Table(new float[]{2, 3, 2});
            table.setWidth(500);
            table.addCell(cabecera("Documento"));
            table.addCell(cabecera("Cliente"));
            table.addCell(cabecera("Puntos"));
            while (rs.next()) {
                table.addCell(rs.getString("documento"));
                table.addCell(rs.getString("cliente"));
                table.addCell(String.valueOf(rs.getInt("puntos")));
            }
            doc.add(table);
        }
    }

    private void reporteCanjes(Connection con, Document doc) throws SQLException {
        String sql = "SELECT c.documento, CONCAT(c.nombre, ' ', c.apellidos) AS cliente, " +
                     "r.nombre AS recompensa, mp.fecha, mp.puntos AS puntos_usados " +
                     "FROM movimientos_puntos mp " +
                     "JOIN clientes c ON mp.id_cliente = c.id_cliente " +
                     "JOIN recompensas r ON mp.id_recompensa = r.id_recompensa " +
                     "WHERE mp.tipo = 'CANJE' ORDER BY mp.fecha DESC";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            Table table = new Table(new float[]{2, 3, 3, 2, 1});
            table.setWidth(550);
            table.addCell(cabecera("Documento"));
            table.addCell(cabecera("Cliente"));
            table.addCell(cabecera("Recompensa"));
            table.addCell(cabecera("Fecha"));
            table.addCell(cabecera("Pts"));
            while (rs.next()) {
                table.addCell(rs.getString("documento"));
                table.addCell(rs.getString("cliente"));
                table.addCell(rs.getString("recompensa"));
                table.addCell(rs.getString("fecha"));
                table.addCell(String.valueOf(rs.getInt("puntos_usados")));
            }
            doc.add(table);
        }
    }

    private void reporteMenusVendidos(Connection con, Document doc) throws SQLException {
        String sql = "SELECT m.codigo, m.nombre AS menu, SUM(dp.cantidad) AS veces_vendido, " +
                     "SUM(dp.cantidad * dp.precio_unitario) AS total_recaudado " +
                     "FROM detalle_pedido dp JOIN menus m ON dp.id_menu = m.id_menu " +
                     "JOIN pedidos p ON dp.id_pedido = p.id_pedido " +
                     "WHERE p.estado IN ('PAGADO', 'ENTREGADO') " +
                     "GROUP BY m.id_menu, m.codigo, m.nombre ORDER BY veces_vendido DESC";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            Table table = new Table(new float[]{2, 3, 2, 2});
            table.setWidth(500);
            table.addCell(cabecera("Código"));
            table.addCell(cabecera("Menú"));
            table.addCell(cabecera("Veces Vendido"));
            table.addCell(cabecera("Total Recaudado ($)"));
            while (rs.next()) {
                table.addCell(rs.getString("codigo"));
                table.addCell(rs.getString("menu"));
                table.addCell(String.valueOf(rs.getInt("veces_vendido")));
                table.addCell(String.format("%.2f", rs.getDouble("total_recaudado")));
            }
            doc.add(table);
        }
    }

    private Cell cabecera(String texto) {
        return new Cell().add(new Paragraph(texto).setBold()).setTextAlignment(TextAlignment.CENTER);
    }
}