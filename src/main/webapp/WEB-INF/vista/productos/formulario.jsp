<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/vista/comun/encabezado.jspf" %>
<c:set var="editando" value="${producto.id > 0}"/>
<h1>${editando ? 'Editar producto' : 'Nuevo producto'}</h1>

<c:if test="${not empty error}">
    <p class="error">${error}</p>
</c:if>

<form class="formulario" method="post" action="${pageContext.request.contextPath}/productos">
    <input type="hidden" name="accion" value="guardar">
    <c:if test="${editando}">
        <input type="hidden" name="id" value="${producto.id}">
    </c:if>

    <label>Codigo
        <input type="text" name="codigo" value="${producto.codigo}" maxlength="20" required>
    </label>

    <label>Categoria
        <c:choose>
            <c:when test="${editando}">
                <input type="text" value="${producto.categoria.etiqueta}" disabled>
                <input type="hidden" name="categoria" value="${producto.categoria.name()}">
            </c:when>
            <c:otherwise>
                <select name="categoria">
                    <c:forEach var="cat" items="${categorias}">
                        <option value="${cat.name()}" ${producto.categoria == cat ? 'selected' : ''}>${cat.etiqueta}</option>
                    </c:forEach>
                </select>
            </c:otherwise>
        </c:choose>
    </label>

    <label>Nombre
        <input type="text" name="nombre" value="${producto.nombre}" maxlength="100" required>
    </label>

    <label>Precio
        <input type="number" name="precio" value="${producto.precio}" min="0" step="0.01" required>
    </label>

    <c:choose>
        <c:when test="${editando}">
            <p>Existencia actual: ${producto.existencia}. Para agregar unidades usa la pantalla de inventario.</p>
        </c:when>
        <c:otherwise>
            <label>Existencia inicial
                <input type="number" name="existencia" value="${empty producto ? 0 : producto.existencia}" min="0" required>
            </label>
        </c:otherwise>
    </c:choose>

    <p>
        <button type="submit">Guardar</button>
        <a class="boton secundario" href="${pageContext.request.contextPath}/productos">Cancelar</a>
    </p>
</form>
<%@ include file="/WEB-INF/vista/comun/pie.jspf" %>
