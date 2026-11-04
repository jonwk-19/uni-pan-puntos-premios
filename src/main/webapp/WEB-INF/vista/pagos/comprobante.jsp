
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.panpuntos.modelo.Pago"%>
<%@page import="com.panpuntos.modelo.Pedido"%>
<%@page import="com.panpuntos.modelo.MetodoPago"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Comprobante de pago</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f5f5f5;
            margin: 30px;
        }

        .comprobante {
            max-width: 550px;
            margin: auto;
            padding: 30px;
            background: white;
            border: 1px solid #ddd;
            border-radius: 8px;
        }

        h1 {
            color: #5c3825;
            text-align: center;
        }

        .aprobado {
            color: green;
            font-weight: bold;
            text-align: center;
        }

        .dato {
            padding: 10px 0;
            border-bottom: 1px solid #eee;
        }

        .total {
            font-size: 22px;
            font-weight: bold;
            text-align: right;
            margin-top: 20px;
        }

        a, button {
            display: inline-block;
            padding: 10px 15px;
            margin-top: 20px;
            background: #6b4226;
            color: white;
            border: none;
            border-radius: 4px;
            text-decoration: none;
            cursor: pointer;
        }

        @media print {
            a, button {
                display: none;
            }
        }
    </style>
</head>
<body>

<%
    Pago pago = (Pago) request.getAttribute("pago");
    Pedido pedido = (Pedido) request.getAttribute("pedido");

    if (pago == null || pedido == null) {
%>
    <h2>No hay información del comprobante.</h2>
<%
    } else {
%>

<div class="comprobante">

    <h1>Pan, Puntos y Premios</h1>
    <h2 style="text-align:center;">Comprobante de pago</h2>

    <p class="aprobado">PAGO <%= pago.getEstado() %></p>

    <div class="dato">
        <strong>Número de pago:</strong> <%= pago.getId() %>
    </div>

    <div class="dato">
        <strong>Pedido:</strong> #<%= pedido.getId() %>
    </div>

    <div class="dato">
        <strong>Fecha:</strong> <%= pago.getFecha() %>
    </div>

    <div class="dato">
        <strong>Método:</strong> <%= pago.getMetodo() %>
    </div>

    <% if (pago.getMetodo() == MetodoPago.EFECTIVO) { %>

        <div class="dato">
            <strong>Monto recibido:</strong>
            Q <%= String.format("%.2f", pago.getMontoRecibido()) %>
        </div>

        <div class="dato">
            <strong>Cambio:</strong>
            Q <%= String.format("%.2f", pago.getCambio()) %>
        </div>

    <% } else { %>

        <div class="dato">
            <strong>Referencia:</strong> <%= pago.getReferencia() %>
        </div>

    <% } %>

    <div class="total">
        Total: Q <%= String.format("%.2f", pago.getMonto()) %>
    </div>

    <div class="dato">
        <strong>Puntos ganados:</strong>
        <%= pedido.getPuntosGanados() %>
    </div>

    <div style="text-align:center;">
        <button onclick="window.print()">Imprimir comprobante</button>

        <a href="<%= request.getContextPath() %>/pedidos">
            Volver a pedidos
        </a>
    </div>

</div>

<%
    }
%>

</body>
</html>
