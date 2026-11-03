
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/vista/comun/encabezado.jspf" %>

<section class="seccion">

    <div class="encabezado-seccion">
        <div>
            <h1>Clientes</h1>
            <p>Administración de clientes y programa de fidelización.</p>
        </div>

        <a class="boton"
           href="${pageContext.request.contextPath}/clientes?accion=nuevo">
            + Nuevo cliente
        </a>
    </div>

    <!-- Buscador de clientes -->
    <form method="get"
          action="${pageContext.request.contextPath}/clientes"
          class="formulario-busqueda">

        <input type="text"
               name="buscar"
               placeholder="Buscar por nombre o apellidos..."
               value="${busqueda}">

        <button type="submit" class="boton">
            Buscar
        </button>

        <a class="boton secundario"
           href="${pageContext.request.contextPath}/clientes">
            Limpiar
        </a>
    </form>

    <!-- Tabla de clientes -->
    <div class="tabla-contenedor">

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Documento</th>
                    <th>Nombre completo</th>
                    <th>Teléfono</th>
                    <th>Correo</th>
                    <th>Puntos</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
            </thead>

            <tbody>

                <c:forEach var="cliente" items="${clientes}">

                    <tr>
                        <td>${cliente.id}</td>
                        <td>${cliente.documento}</td>
                        <td>${cliente.nombreCompleto}</td>
                        <td>${cliente.telefono}</td>
                        <td>${cliente.email}</td>
                        <td>${cliente.puntos}</td>

                        <td>
                            <c:choose>
                                <c:when test="${cliente.activo}">
                                    <span>Activo</span>
                                </c:when>

                                <c:otherwise>
                                    <span class="inactivo">Inactivo</span>
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <td>

                            <!-- Editar cliente -->
                            <a class="boton secundario"
                               href="${pageContext.request.contextPath}/clientes?accion=editar&id=${cliente.id}">
                                Editar
                            </a>

                            <!-- Consultar historial -->
                            <a class="boton secundario"
                               href="${pageContext.request.contextPath}/historial-puntos?idCliente=${cliente.id}">
                                Historial
                            </a>

                            <!-- Activar o desactivar cliente -->
                            <form method="post"
                                  action="${pageContext.request.contextPath}/clientes"
                                  style="display:inline">

                                <input type="hidden"
                                       name="accion"
                                       value="estado">

                                <input type="hidden"
                                       name="id"
                                       value="${cliente.id}">

                                <input type="hidden"
                                       name="activo"
                                       value="${!cliente.activo}">

                                <button type="submit"
                                        class="boton secundario">

                                    <c:choose>
                                        <c:when test="${cliente.activo}">
                                            Desactivar
                                        </c:when>

                                        <c:otherwise>
                                            Activar
                                        </c:otherwise>
                                    </c:choose>

                                </button>
                            </form>

                        </td>
                    </tr>

                </c:forEach>

                <c:if test="${empty clientes}">
                    <tr>
                        <td colspan="8">
                            No se encontraron clientes.
                        </td>
                    </tr>
                </c:if>

            </tbody>
        </table>

    </div>

</section>

<%@ include file="/WEB-INF/vista/comun/pie.jspf" %>

