<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/vista/comun/encabezado.jspf" %>
<h1>Reportes PDF</h1>
<p>Selecciona el reporte que deseas descargar:</p>
<div class="tarjetas">
    <a class="tarjeta" href="${pageContext.request.contextPath}/reportes?tipo=ventas_periodo">1. Ventas por Período</a>
    <a class="tarjeta" href="${pageContext.request.contextPath}/reportes?tipo=productos_vendidos">2. Productos Más Vendidos</a>
    <a class="tarjeta" href="${pageContext.request.contextPath}/reportes?tipo=bajo_stock">3. Productos Bajo Stock</a>
    <a class="tarjeta" href="${pageContext.request.contextPath}/reportes?tipo=puntos_cliente">4. Puntos por Cliente</a>
    <a class="tarjeta" href="${pageContext.request.contextPath}/reportes?tipo=canjes">5. Historial de Canjes</a>
    <a class="tarjeta" href="${pageContext.request.contextPath}/reportes?tipo=menus_vendidos">6. Ventas de Menús</a>
</div>
<%@ include file="/WEB-INF/vista/comun/pie.jspf" %>