
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/vista/comun/encabezado.jspf" %>

<section class="seccion">

    <div class="encabezado-seccion">
        <div>
            <h1>Catálogo de recompensas</h1>
            <p>Canjea tus puntos por productos y menús gratuitos.</p>
        </div>
    </div>

    <c:if test="${not empty mensaje}">
        <div class="mensaje-exito">
            ${mensaje}
        </div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="mensaje-error">
            ${error}
        </div>
    </c:if>

    <div class="formulario">

        <h2>Realizar un canje</h2>

        <form method="post"
              action="${pageContext.request.contextPath}/recompensas">

            <label for="idCliente">Seleccionar cliente:</label>

            <select name="idCliente" id="idCliente" required>
                <option value="">Selecciona un cliente</option>

                <c:forEach var="cliente" items="${clientes}">
                    <c:if test="${cliente.activo}">
                        <option value="${cliente.id}"
                            ${idClienteSeleccionado == cliente.id ? 'selected' : ''}>
                            ${cliente.nombreCompleto} - ${cliente.puntos} puntos
                        </option>
                    </c:if>
                </c:forEach>
            </select>

            <label for="idRecompensa">Seleccionar recompensa:</label>

            <select name="idRecompensa" id="idRecompensa" required>
                <option value="">Selecciona una recompensa</option>

                <c:forEach var="recompensa" items="${recompensas}">
                    <option value="${recompensa.id}">
                        ${recompensa.nombre} -
                        ${recompensa.puntosRequeridos} puntos
                    </option>
                </c:forEach>
            </select>

            <button type="submit" class="boton">
                Confirmar canje
            </button>

        </form>
    </div>

    <h2>Recompensas disponibles</h2>

    <div class="tabla-contenedor">

        <table>
            <thead>
                <tr>
                    <th>Recompensa</th>
                    <th>Puntos necesarios</th>
                    <th>Estado</th>
                </tr>
            </thead>

            <tbody>

                <c:forEach var="recompensa" items="${recompensas}">
                    <tr>
                        <td>${recompensa.nombre}</td>
                        <td>${recompensa.puntosRequeridos}</td>
                        <td>Disponible</td>
                    </tr>
                </c:forEach>

                <c:if test="${empty recompensas}">
                    <tr>
                        <td colspan="3">
                            No hay recompensas disponibles.
                        </td>
                    </tr>
                </c:if>

            </tbody>
        </table>

    </div>

    <a href="${pageContext.request.contextPath}/clientes"
       class="boton secundario">
        Volver a clientes
    </a>

</section>

<%@ include file="/WEB-INF/vista/comun/pie.jspf" %>

