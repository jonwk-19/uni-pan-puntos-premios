<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/vista/comun/encabezado.jspf" %>
<h1>Ocurrio un problema</h1>
<p class="error">${error}</p>
<p><a class="boton" href="javascript:history.back()">Volver</a></p>
<%@ include file="/WEB-INF/vista/comun/pie.jspf" %>
