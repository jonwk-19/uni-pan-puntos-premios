<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/vista/comun/encabezado.jspf" %>
<h1>Productos</h1>

<form method="get" action="${pageContext.request.contextPath}/productos">
    <label>Categoria
        <select name="categoria" onchange="this.form.submit()">
            <option value="">Todas</option>
            <c:forEach var="cat" items="${categorias}">
                <option value="${cat.name()}" ${cat.name() == filtro ? 'selected' : ''}>${cat.etiqueta}</option>
            </c:forEach>
        </select>
    </label>
    <a class="boton" href="${pageContext.request.contextPath}/productos?accion=nuevo">Nuevo producto</a>
</form>
<br>

<table>
    <tr>
        <th>Codigo</th>
        <th>Categoria</th>
        <th>Nombre</th>
        <th>Precio</th>
        <th>Existencia</th>
        <th>Estado</th>
        <th>Acciones</th>
    </tr>
    <c:forEach var="p" items="${productos}">
        <tr class="${p.activo ? '' : 'inactivo'}">
            <td>${p.codigo}</td>
            <td>${p.categoria.etiqueta}</td>
            <td>${p.nombre}</td>
            <td>$<fmt:formatNumber value="${p.precio}" minFractionDigits="2" maxFractionDigits="2"/></td>
            <td>${p.existencia}</td>
            <td>${p.activo ? 'Activo' : 'Desactivado'}</td>
            <td>
                <a class="boton" href="${pageContext.request.contextPath}/productos?accion=editar&id=${p.id}">Editar</a>
                <a class="boton secundario" href="${pageContext.request.contextPath}/inventario?id=${p.id}">Inventario</a>
                <form method="post" action="${pageContext.request.contextPath}/productos" style="display:inline">
                    <input type="hidden" name="accion" value="estado">
                    <input type="hidden" name="id" value="${p.id}">
                    <input type="hidden" name="activo" value="${!p.activo}">
                    <button type="submit">${p.activo ? 'Desactivar' : 'Activar'}</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty productos}">
        <tr><td colspan="7">No hay productos registrados.</td></tr>
    </c:if>
</table>
<%@ include file="/WEB-INF/vista/comun/pie.jspf" %>
