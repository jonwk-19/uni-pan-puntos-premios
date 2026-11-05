
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.panpuntos.modelo.Pedido"%>
<%@page import="com.panpuntos.modelo.MetodoPago"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Procesar pago</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 30px;
            background: #f5f5f5;
        }

        .contenedor {
            max-width: 500px;
            margin: auto;
            background: white;
            padding: 25px;
            border-radius: 8px;
        }

        h1 { color: #5c3825; }

        label {
            display: block;
            margin-top: 15px;
        }

        input, select {
            width: 100%;
            padding: 10px;
            margin-top: 5px;
            box-sizing: border-box;
        }

        button, a {
            display: inline-block;
            margin-top: 20px;
            padding: 10px 15px;
            border: none;
            background: #6b4226;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            cursor: pointer;
        }

        .error { color: red; }

        .simulacion {
            padding: 12px;
            margin-top: 15px;
            background: #f8f1eb;
            border-radius: 5px;
        }
    </style>
</head>

<body>

<div class="contenedor">

<%
    Pedido pedido = (Pedido) request.getAttribute("pedido");
    String error = (String) request.getAttribute("error");

    if (pedido == null) {
%>

    <h2>No se encontró el pedido.</h2>

<%
    } else {
%>

    <h1>Procesar pago</h1>

    <% if (error != null) { %>
        <p class="error"><%= error %></p>
    <% } %>

    <p><strong>Pedido:</strong> #<%= pedido.getId() %></p>

    <p>
        <strong>Total a pagar:</strong>
        Q <%= String.format("%.2f", pedido.getTotal()) %>
    </p>

    <form action="<%= request.getContextPath() %>/pagos" method="post">

        <input type="hidden" name="idPedido"
               value="<%= pedido.getId() %>">

        <label>Método de pago:</label>

        <select name="metodo" id="metodo" required
                onchange="mostrarCampos()">

            <option value="">Selecciona un método</option>
            <option value="EFECTIVO">Efectivo</option>
            <option value="TARJETA">Tarjeta</option>

        </select>

        <div id="campoEfectivo" style="display:none;">

            <label>Dinero recibido (Q):</label>

            <input type="number"
                   name="montoRecibido"
                   id="montoRecibido"
                   min="<%= pedido.getTotal() %>"
                   step="0.01">

        </div>

        <div id="campoTarjeta" class="simulacion"
             style="display:none;">

            <label>Resultado de tarjeta simulada:</label>

            <select name="tarjetaAprobada" id="tarjetaAprobada">

                <option value="true">Tarjeta aprobada</option>
                <option value="false">Tarjeta rechazada</option>

            </select>

            <p>
                <small>
                    Esta opción sirve para probar ambos resultados
                    del pago con tarjeta.
                </small>
            </p>

        </div>

        <button type="submit">Procesar pago</button>

    </form>

    <a href="<%= request.getContextPath() %>/pedidos">
        Cancelar
    </a>

<%
    }
%>

</div>

<script>
    function mostrarCampos() {

        const metodo = document.getElementById("metodo").value;

        const campoEfectivo = document.getElementById("campoEfectivo");
        const campoTarjeta = document.getElementById("campoTarjeta");

        const monto = document.getElementById("montoRecibido");
        const resultadoTarjeta = document.getElementById("tarjetaAprobada");

        campoEfectivo.style.display =
                metodo === "EFECTIVO" ? "block" : "none";

        campoTarjeta.style.display =
                metodo === "TARJETA" ? "block" : "none";

        monto.required = metodo === "EFECTIVO";
        resultadoTarjeta.required = metodo === "TARJETA";
    }
</script>

</body>
</html>
