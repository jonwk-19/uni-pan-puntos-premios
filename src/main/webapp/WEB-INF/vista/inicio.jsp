<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/vista/comun/encabezado.jspf" %>
<h1>Pan, Puntos y Premios</h1>
<p>Sistema de pedidos y fidelizacion del restaurante.</p>
<div class="tarjetas">
    <a class="tarjeta" href="${pageContext.request.contextPath}/productos">Productos</a>
    <a class="tarjeta" href="${pageContext.request.contextPath}/inventario">Inventario</a>
    <a class="tarjeta" href="${pageContext.request.contextPath}/clientes">Clientes</a>
    <a class="tarjeta" href="${pageContext.request.contextPath}/pedidos">Pedidos</a>
    <a class="tarjeta" href="${pageContext.request.contextPath}/reportes">Reportes</a>
</div>
<%@ include file="/WEB-INF/vista/comun/pie.jspf" %>
