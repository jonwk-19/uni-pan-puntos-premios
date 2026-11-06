
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/vista/comun/encabezado.jspf" %>

<section class="seccion">

    <div class="encabezado-seccion">
        <div>
            <h1>
                <c:choose>
                    <c:when test="${not empty cliente}">
                        Editar cliente
                    </c:when>
                    <c:otherwise>
                        Registrar cliente
                    </c:otherwise>
                </c:choose>
            </h1>

            <p>Completa los datos del cliente.</p>
        </div>
    </div>

    <c:if test="${not empty error}">
        <p class="aviso">${error}</p>
    </c:if>

    <form method="post"
          action="${pageContext.request.contextPath}/clientes"
          class="formulario">

        <input type="hidden" name="accion" value="guardar">

        <c:if test="${not empty cliente}">
            <input type="hidden" name="id" value="${cliente.id}">
        </c:if>

        <div class="campo">
            <label for="documento">Documento de identidad *</label>
            <input type="text"
                   id="documento"
                   name="documento"
                   maxlength="20"
                   value="${cliente.documento}"
                   required>
        </div>

        <div class="campo">
            <label for="nombre">Nombre *</label>
            <input type="text"
                   id="nombre"
                   name="nombre"
                   maxlength="100"
                   value="${cliente.nombre}"
                   required>
        </div>

        <div class="campo">
            <label for="apellidos">Apellidos *</label>
            <input type="text"
                   id="apellidos"
                   name="apellidos"
                   maxlength="100"
                   value="${cliente.apellidos}"
                   required>
        </div>

        <div class="campo">
            <label for="telefono">Teléfono</label>
            <input type="text"
                   id="telefono"
                   name="telefono"
                   maxlength="20"
                   value="${cliente.telefono}">
        </div>

        <div class="campo">
            <label for="email">Correo electrónico</label>
            <input type="email"
                   id="email"
                   name="email"
                   maxlength="100"
                   value="${cliente.email}">
        </div>

        <div class="acciones">
            <button type="submit" class="boton">
                Guardar cliente
            </button>

            <a class="boton secundario"
               href="${pageContext.request.contextPath}/clientes">
                Cancelar
            </a>
        </div>

    </form>

    <p class="ayuda">* Los campos marcados son obligatorios.</p>

</section>

<%@ include file="/WEB-INF/vista/comun/pie.jspf" %>
