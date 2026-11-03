
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.panpuntos.modelo.MovimientoPuntos" %>

<%
    List<MovimientoPuntos> movimientos =
        (List<MovimientoPuntos>) request.getAttribute("movimientos");

    Integer idCliente =
        (Integer) request.getAttribute("idCliente");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Historial de puntos</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f5f5f5;
            margin: 0;
            padding: 30px;
            color: #333;
        }

        .contenedor {
            max-width: 1000px;
            margin: auto;
            background: white;
            padding: 25px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,.08);
        }

        h1 {
            color: #713f12;
        }

        .subtitulo {
            color: #666;
            margin-bottom: 25px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th, td {
            padding: 13px;
            border-bottom: 1px solid #ddd;
            text-align: left;
        }

        th {
            background: #713f12;
            color: white;
        }

        .acumulacion {
            color: #16803d;
            font-weight: bold;
        }

        .canje {
            color: #c0392b;
            font-weight: bold;
        }

        .vacio {
            text-align: center;
            padding: 30px;
            color: #777;
        }

        .boton {
            display: inline-block;
            padding: 10px 16px;
            background: #713f12;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            margin-top: 20px;
        }

        .boton:hover {
            background: #542e0c;
        }

        @media (max-width: 700px) {
            body {
                padding: 10px;
            }

            .contenedor {
                padding: 12px;
                overflow-x: auto;
            }

            table {
                min-width: 650px;
            }
        }
    </style>
</head>

<body>

<div class="contenedor">

    <h1>Historial de puntos</h1>

    <p class="subtitulo">
        Movimientos del cliente número:
        <strong><%= idCliente %></strong>
    </p>

    <table>
        <thead>
            <tr>
                <th>Fecha</th>
                <th>Tipo</th>
                <th>Puntos</th>
                <th>ID Pedido</th>
                <th>ID Recompensa</th>
            </tr>
        </thead>

        <tbody>

        <% if (movimientos != null && !movimientos.isEmpty()) { %>

            <% for (MovimientoPuntos movimiento : movimientos) { %>

                <tr>
                    <td><%= movimiento.getFechaTexto() %></td>

                    <td>
                        <% if (movimiento.getTipo() != null
                                && movimiento.getTipo().name().equals("ACUMULACION")) { %>

                            <span class="acumulacion">Acumulación</span>

                        <% } else { %>

                            <span class="canje">Canje</span>

                        <% } %>
                    </td>

                    <td>
                        <strong><%= movimiento.getPuntos() %></strong>
                    </td>

                    <td>
                        <%= movimiento.getIdPedido() != null
                            ? movimiento.getIdPedido() : "-" %>
                    </td>

                    <td>
                        <%= movimiento.getIdRecompensa() != null
                            ? movimiento.getIdRecompensa() : "-" %>
                    </td>
                </tr>

            <% } %>

        <% } else { %>

            <tr>
                <td colspan="5" class="vacio">
                    Este cliente todavía no tiene movimientos de puntos.
                </td>
            </tr>

        <% } %>

        </tbody>
    </table>

    <a href="<%= request.getContextPath() %>/clientes" class="boton">
        Volver a clientes
    </a>

</div>

</body>
</html>
