
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.panpuntos.modelo.Pedido"%>
<%@page import="com.panpuntos.modelo.EstadoPedido"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Pedidos - Pan, Puntos y Premios</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 30px;
            background: #f5f5f5;
        }

        h1 { color: #5c3825; }

        .nuevo {
            display: inline-block;
            padding: 10px 16px;
            background: #287a43;
            color: white;
            border-radius: 5px;
            text-decoration: none;
            margin-bottom: 18px;
        }

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

        a, button {
            padding: 7px 10px;
            text-decoration: none;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        .detalle { background: #2878a5; color: white; }
        .pagar { background: #238b45; color: white; }
        .anular { background: #bd3434; color: white; }

        form { display: inline; }

        .mensaje { color: green; }
        .error { color: red; }
    </style>
</head>
<body>

<h1>Gestión de pedidos</h1>

<a class="nuevo"
   href="<%= request.getContextPath() %>/pedidos?accion=nuevo">
    + Nuevo pedido
</a>

<p class="mensaje">
    <%= request.getSession().getAttribute("mensaje") != null
        ? request.getSession().getAttribute("mensaje") : "" %>
</p>

<%
    request.getSession().removeAttribute("mensaje");
%>

<%
    String error = (String) request.getAttribute("error");
    if (error != null) {
%>
    <p class="error"><%= error %></p>
<%
    }
%>

<table>
    <thead>
        <tr>
            <th>ID</th>
            <th>Fecha</th>
            <th>Cliente</th>
            <th>Estado</th>
            <th>Total</th>
            <th>Puntos</th>
            <th>Acciones</th>
        </tr>
    </thead>

    <tbody>
    <%
        List<Pedido> pedidos =
            (List<Pedido>) request.getAttribute("pedidos");

        if (pedidos != null && !pedidos.isEmpty()) {
            for (Pedido pedido : pedidos) {
    %>
        <tr>
            <td><%= pedido.getId() %></td>
            <td><%= pedido.getFecha() %></td>
            <td>
                <%= pedido.getIdCliente() != null
                    ? pedido.getIdCliente() : "Sin cliente" %>
            </td>
            <td><%= pedido.getEstado() %></td>
            <td>Q <%= String.format("%.2f", pedido.getTotal()) %></td>
            <td><%= pedido.getPuntosGanados() %></td>
            <td>
                <a class="detalle"
                   href="<%= request.getContextPath() %>/pedidos?accion=detalle&id=<%= pedido.getId() %>">
                    Detalle
                </a>

                <% if (pedido.getEstado() == EstadoPedido.PENDIENTE) { %>
                    <a class="pagar"
                       href="<%= request.getContextPath() %>/pagos?id=<%= pedido.getId() %>">
                        Pagar
                    </a>

                    <form action="<%= request.getContextPath() %>/pedidos"
                          method="post"
                          onsubmit="return confirm('¿Deseas anular este pedido?');">
                        <input type="hidden" name="id"
                               value="<%= pedido.getId() %>">
                        <input type="hidden" name="accion" value="anular">
                        <button class="anular" type="submit">Anular</button>
                    </form>

                <% } else if (pedido.getEstado() == EstadoPedido.PAGADO) { %>
                    <form action="<%= request.getContextPath() %>/pedidos"
                          method="post">
                        <input type="hidden" name="id"
                               value="<%= pedido.getId() %>">
                        <input type="hidden" name="accion" value="entregar">
                        <button class="pagar" type="submit">Entregar</button>
                    </form>
                <% } %>
            </td>
        </tr>
    <%
            }
        } else {
    %>
        <tr>
            <td colspan="7">No hay pedidos registrados.</td>
        </tr>
    <%
        }
    %>
    </tbody>
</table>

<br>
<a href="<%= request.getContextPath() %>/inicio">Volver al inicio</a>

</body>
</html>
