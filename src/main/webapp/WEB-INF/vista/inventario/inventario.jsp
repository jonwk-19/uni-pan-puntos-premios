<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/vista/comun/encabezado.jspf" %>
<h1>Inventario</h1>

<c:if test="${not empty error}">
    <p class="error">${error}</p>
</c:if>

<h2>Registrar entrada</h2>
<form class="formulario" method="post" action="${pageContext.request.contextPath}/inventario">
    <label>Producto
        <select name="idProducto" required>
            <c:forEach var="p" items="${productos}">
                <c:if test="${p.activo}">
                    <option value="${p.id}" ${p.id == seleccionado ? 'selected' : ''}>${p.codigo} - ${p.nombre} (existencia: ${p.existencia})</option>
                </c:if>
            </c:forEach>
        </select>
    </label>
    <label>Cantidad
        <input type="number" name="cantidad" min="1" required>
    </label>
    <p><button type="submit">Registrar entrada</button></p>
</form>

<h2>Historial de movimientos</h2>
<c:if test="${not empty seleccionado}">
    <p><a class="boton secundario" href="${pageContext.request.contextPath}/inventario">Ver todos los productos</a></p>
</c:if>
<table>
    <tr>
        <th>Fecha</th>
        <th>Producto</th>
        <th>Tipo</th>
        <th>Cantidad</th>
        <th>Pedido</th>
    </tr>
    <c:forEach var="m" items="${movimientos}">
        <tr>
            <td>${m.fechaTexto}</td>
            <td>${m.nombreProducto}</td>
            <td>${m.tipo}</td>
            <td>${m.cantidad}</td>
            <td>${empty m.idPedido ? '-' : m.idPedido}</td>
        </tr>
    </c:forEach>
    <c:if test="${empty movimientos}">
        <tr><td colspan="5">Sin movimientos registrados.</td></tr>
    </c:if>
</table>
<%@ include file="/WEB-INF/vista/comun/pie.jspf" %>
