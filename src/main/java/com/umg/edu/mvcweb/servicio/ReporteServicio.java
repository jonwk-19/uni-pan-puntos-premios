package com.umg.edu.mvcweb.servicio;

import com.umg.edu.mvcweb.modelo.ReporteDatos;
import com.umg.edu.mvcweb.util.ConexionDB;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

@Service
public class ReporteServicio {

    public ReporteDatos obtenerReporte(
            String tipo, LocalDate inicio, LocalDate fin)
            throws SQLException {

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Tipo de reporte no válido.");
        }

        switch (tipo) {

            case "ventas_periodo":
                return reporteVentasPeriodo(inicio, fin);

            case "productos_vendidos":
                return reporteProductosVendidos();

            case "bajo_stock":
                return reporteBajoStock();

            case "puntos_cliente":
                return reportePuntosCliente();

            case "canjes":
                return reporteCanjes();

            case "menus_vendidos":
                return reporteMenusVendidos();

            default:
                throw new IllegalArgumentException(
                        "El tipo de reporte solicitado no existe.");
        }
    }

    private ReporteDatos reporteVentasPeriodo(
            LocalDate inicio, LocalDate fin) throws SQLException {

        if (inicio == null || fin == null || inicio.isAfter(fin)) {
            throw new IllegalArgumentException(
                    "El período seleccionado no es válido.");
        }

        String sql =
                "SELECT DATE(p.fecha) AS fecha, " +
                "SUM(CASE WHEN pg.metodo = 'EFECTIVO' " +
                "THEN pg.monto ELSE 0 END) AS efectivo, " +
                "SUM(CASE WHEN pg.metodo = 'TARJETA' " +
                "THEN pg.monto ELSE 0 END) AS tarjeta " +
                "FROM pedidos p " +
                "JOIN pagos pg ON p.id_pedido = pg.id_pedido " +
                "WHERE p.estado IN ('PAGADO', 'ENTREGADO') " +
                "AND pg.estado = 'APROBADO' " +
                "AND DATE(p.fecha) BETWEEN ? AND ? " +
                "GROUP BY DATE(p.fecha) " +
                "ORDER BY fecha";

        ReporteDatos reporte = crear(
                "ventas_periodo",
                "Ventas por Período (Efectivo vs Tarjeta)",
                "Fecha", "Efectivo", "Tarjeta"
        );

        reporte.setPeriodo(inicio + " a " + fin);

        consultar(sql, reporte, rs -> Arrays.asList(
                rs.getString("fecha"),
                dinero(rs.getDouble("efectivo")),
                dinero(rs.getDouble("tarjeta"))
        ), Date.valueOf(inicio), Date.valueOf(fin));

        return reporte;
    }

    private ReporteDatos reporteProductosVendidos()
            throws SQLException {

        String sql =
                "SELECT pr.nombre, pr.categoria, " +
                "SUM(dp.cantidad) AS total_vendido " +
                "FROM detalle_pedido dp " +
                "JOIN productos pr ON dp.id_producto = pr.id_producto " +
                "JOIN pedidos p ON dp.id_pedido = p.id_pedido " +
                "WHERE p.estado IN ('PAGADO', 'ENTREGADO') " +
                "GROUP BY pr.id_producto, pr.nombre, pr.categoria " +
                "ORDER BY total_vendido DESC";

        ReporteDatos reporte = crear(
                "productos_vendidos",
                "Productos Más Vendidos",
                "Producto", "Categoría", "Cantidad Vendida"
        );

        consultar(sql, reporte, rs -> Arrays.asList(
                rs.getString("nombre"),
                rs.getString("categoria"),
                String.valueOf(rs.getInt("total_vendido"))
        ));

        return reporte;
    }

    private ReporteDatos reporteBajoStock()
            throws SQLException {

        String sql =
                "SELECT codigo, nombre, categoria, existencia " +
                "FROM productos " +
                "WHERE activo = TRUE AND existencia <= 5 " +
                "ORDER BY existencia ASC";

        ReporteDatos reporte = crear(
                "bajo_stock",
                "Productos Agotados o con Existencia Menor o Igual a 5",
                "Código", "Nombre", "Categoría", "Existencia"
        );

        consultar(sql, reporte, rs -> Arrays.asList(
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("categoria"),
                String.valueOf(rs.getInt("existencia"))
        ));

        return reporte;
    }

    private ReporteDatos reportePuntosCliente()
            throws SQLException {

        String sql =
                "SELECT c.documento, " +
                "CONCAT(c.nombre, ' ', c.apellidos) AS cliente, " +
                "c.puntos " +
                "FROM clientes c " +
                "WHERE c.activo = TRUE " +
                "ORDER BY c.puntos DESC";

        ReporteDatos reporte = crear(
                "puntos_cliente",
                "Puntos Acumulados por Cliente",
                "Documento", "Cliente", "Puntos"
        );

        consultar(sql, reporte, rs -> Arrays.asList(
                rs.getString("documento"),
                rs.getString("cliente"),
                String.valueOf(rs.getInt("puntos"))
        ));

        return reporte;
    }

    private ReporteDatos reporteCanjes()
            throws SQLException {

        String sql =
                "SELECT c.documento, " +
                "CONCAT(c.nombre, ' ', c.apellidos) AS cliente, " +
                "r.nombre AS recompensa, " +
                "mp.fecha, mp.puntos AS puntos_usados " +
                "FROM movimientos_puntos mp " +
                "JOIN clientes c ON mp.id_cliente = c.id_cliente " +
                "JOIN recompensas r ON mp.id_recompensa = r.id_recompensa " +
                "WHERE mp.tipo = 'CANJE' " +
                "ORDER BY mp.fecha DESC";

        ReporteDatos reporte = crear(
                "canjes",
                "Historial de Canjes",
                "Documento", "Cliente", "Recompensa",
                "Fecha", "Puntos Usados"
        );

        consultar(sql, reporte, rs -> Arrays.asList(
                rs.getString("documento"),
                rs.getString("cliente"),
                rs.getString("recompensa"),
                rs.getString("fecha"),
                String.valueOf(rs.getInt("puntos_usados"))
        ));

        return reporte;
    }

    private ReporteDatos reporteMenusVendidos()
            throws SQLException {

        String sql =
                "SELECT m.codigo, m.nombre AS menu, " +
                "SUM(dp.cantidad) AS veces_vendido, " +
                "SUM(dp.cantidad * dp.precio_unitario) AS total_recaudado " +
                "FROM detalle_pedido dp " +
                "JOIN menus m ON dp.id_menu = m.id_menu " +
                "JOIN pedidos p ON dp.id_pedido = p.id_pedido " +
                "WHERE p.estado IN ('PAGADO', 'ENTREGADO') " +
                "GROUP BY m.id_menu, m.codigo, m.nombre " +
                "ORDER BY veces_vendido DESC";

        ReporteDatos reporte = crear(
                "menus_vendidos",
                "Ventas de Menús Completos",
                "Código", "Menú", "Veces Vendido", "Total Recaudado"
        );

        consultar(sql, reporte, rs -> Arrays.asList(
                rs.getString("codigo"),
                rs.getString("menu"),
                String.valueOf(rs.getInt("veces_vendido")),
                dinero(rs.getDouble("total_recaudado"))
        ));

        return reporte;
    }

    private ReporteDatos crear(
            String tipo, String titulo, String... encabezados) {

        return new ReporteDatos(
                tipo,
                titulo,
                Arrays.asList(encabezados)
        );
    }

    private void consultar(
            String sql,
            ReporteDatos reporte,
            FilaMapper mapper,
            Object... parametros) throws SQLException {

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    reporte.agregarFila(mapper.mapear(rs));
                }
            }
        }
    }

    private String dinero(double cantidad) {
        return String.format(Locale.US, "%.2f", cantidad);
    }

    public byte[] generarPdf(
            String tipo, LocalDate inicio, LocalDate fin)
            throws Exception {

        ReporteDatos reporte = obtenerReporte(tipo, inicio, fin);

        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(salida);
             PdfDocument pdf = new PdfDocument(writer);
             Document documento = new Document(pdf)) {

            documento.setMargins(35, 30, 35, 30);

            documento.add(
                    new Paragraph("Pan, Puntos y Premios")
                            .setFontSize(20)
                            .setBold()
                            .setTextAlignment(TextAlignment.CENTER)
            );

            documento.add(
                    new Paragraph(reporte.getTitulo())
                            .setFontSize(14)
                            .setTextAlignment(TextAlignment.CENTER)
            );

            if (reporte.getPeriodo() != null) {
                documento.add(
                        new Paragraph("Período: " + reporte.getPeriodo())
                );
            }

            documento.add(new Paragraph(" "));

            if (reporte.getFilas().isEmpty()) {
                documento.add(
                        new Paragraph("No hay registros para este reporte.")
                );
            } else {

                float[] anchos =
                        new float[reporte.getEncabezados().size()];

                Arrays.fill(anchos, 1);

                Table tabla = new Table(anchos);
                tabla.setWidth(UnitValue.createPercentValue(100));

                for (String encabezado : reporte.getEncabezados()) {
                    tabla.addHeaderCell(
                            new Cell().add(
                                    new Paragraph(encabezado).setBold()
                            )
                    );
                }

                for (List<String> fila : reporte.getFilas()) {
                    for (String valor : fila) {
                        tabla.addCell(
                                new Cell().add(
                                        new Paragraph(valor == null ? "" : valor)
                                )
                        );
                    }
                }

                documento.add(tabla);
            }

            documento.add(new Paragraph(" "));
            documento.add(
                    new Paragraph("Reporte generado por Pan, Puntos y Premios.")
                            .setFontSize(9)
            );
        }

        return salida.toByteArray();
    }

    @FunctionalInterface
    private interface FilaMapper {
        List<String> mapear(ResultSet rs) throws SQLException;
    }
}
