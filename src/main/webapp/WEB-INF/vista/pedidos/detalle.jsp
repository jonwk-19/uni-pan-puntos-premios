
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.panpuntos.modelo.Pedido"%>
<%@page import="com.panpuntos.modelo.DetallePedido"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Detalle del pedido</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 30px;
            background: #f5f5f5;
        }

        h1 { color: #5c3825; }

        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
        }

        th, td {
            padding: 12px;
            border: 1px solid #ddd;
            text-align: center;
        }

        th {
            background: #6b4226;
            color: white;
        }

        a {
            display: inline-block;
            margin-top: 20px;
            padding: 9px 15px;
            background: #6b4226;
            color: white;
            text-decoration: none;
            border-radius: 4px;
        }
    </style>
</head>
<body>

<%
    Pedido pedido = (Pedido) request.getAttribute("pedido");

    if (pedido == null) {
%>
    <h2>No se encontró el pedido.</h2>
<%
    } else {
%>

<h1>Detalle del pedido #<%= pedido.getId() %></h1>

<p><strong>Estado:</strong> <%= pedido.getEstado() %></p>
<p><strong>Fecha:</strong> <%= pedido.getFecha() %></p>
<p><strong>Total:</strong> Q <%= String.format("%.2f", pedido.getTotal()) %></p>
<p><strong>Puntos ganados:</strong> <%= pedido.getPuntosGanados() %></p>

<table>
    <thead>
        <tr>
            <th>Producto o menú</th>
            <th>Cantidad</th>
            <th>Precio unitario</th>
            <th>Subtotal</th>
        </tr>
    </thead>
    <tbody>
    <%
        for (DetallePedido detalle : pedido.getDetalles()) {
    %>
        <tr>
            <td><%= detalle.getNombre() %></td>
            <td><%= detalle.getCantidad() %></td>
            <td>Q <%= String.format("%.2f", detalle.getPrecioUnitario()) %></td>
            <td>Q <%= String.format("%.2f", detalle.getSubtotal()) %></td>
        </tr>
    <%
        }
    %>
    </tbody>
</table>

<%
    }
%>

<a href="<%= request.getContextPath() %>/pedidos">Volver a pedidos</a>

</body>
</html>
