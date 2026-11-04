
<%@page import="java.util.List"%>
<%@page import="com.panpuntos.modelo.Cliente"%>
<%@page import="com.panpuntos.modelo.Producto"%>
<%@page import="com.panpuntos.modelo.Menu"%>

<%
    List<Cliente> clientes =
            (List<Cliente>) request.getAttribute("clientes");

    List<Producto> productos =
            (List<Producto>) request.getAttribute("productos");

    List<Menu> menus =
            (List<Menu>) request.getAttribute("menus");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nuevo pedido - Pan, Puntos y Premios</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 30px;
            background: #f5f5f5;
        }

        .contenedor {
            max-width: 850px;
            margin: auto;
            background: white;
            padding: 25px;
            border-radius: 10px;
        }

        h2 {
            color: #8b4513;
        }

        .campo {
            margin-bottom: 18px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-weight: bold;
        }

        select, input {
            padding: 9px;
            width: 100%;
            box-sizing: border-box;
        }

        .detalle {
            display: grid;
            grid-template-columns: 3fr 1fr auto;
            gap: 10px;
            margin-bottom: 12px;
        }

        button, .volver {
            padding: 10px 16px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            margin-top: 10px;
        }

        .agregar {
            background: #287a43;
            color: white;
        }

        .guardar {
            background: #8b4513;
            color: white;
        }

        .eliminar {
            background: #b52b27;
            color: white;
        }

        .volver {
            background: #777;
            color: white;
        }

        @media(max-width: 600px) {
            .detalle {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>

<body>

<div class="contenedor">

    <h2>Registrar nuevo pedido</h2>

    <% if (clientes == null || clientes.isEmpty()) { %>

        <p>No hay clientes activos disponibles.</p>

    <% } else if ((productos == null || productos.isEmpty())
            && (menus == null || menus.isEmpty())) { %>

        <p>No hay productos ni menús disponibles.</p>

    <% } else { %>

        <form action="<%= request.getContextPath() %>/pedidos"
              method="post">

            <input type="hidden" name="accion" value="crear">

            <div class="campo">
                <label for="idCliente">Seleccionar cliente:</label>

                <select id="idCliente" name="idCliente" required>
                    <option value="">Seleccione un cliente</option>

                    <% for (Cliente cliente : clientes) { %>
                        <option value="<%= cliente.getId() %>">
                            <%= cliente.getNombre() %>
                            <%= cliente.getApellidos() %>
                            - <%= cliente.getDocumento() %>
                        </option>
                    <% } %>

                </select>
            </div>

            <h3>Productos y menús del pedido</h3>

            <div id="detalles">

                <div class="detalle">

                    <select name="articulo" required>
                        <option value="">Seleccione un artículo</option>

                        <% if (productos != null) {
                            for (Producto producto : productos) {
                                if (producto.isActivo()) { %>

                            <option value="P:<%= producto.getId() %>">
                                Producto: <%= producto.getNombre() %>
                                - Q<%= producto.getPrecio() %>
                                (Stock: <%= producto.getExistencia() %>)
                            </option>

                        <%  }
                            }
                        }

                        if (menus != null) {
                            for (Menu menu : menus) {
                                if (menu.isActivo()) { %>

                            <option value="M:<%= menu.getId() %>">
                                Menú: <%= menu.getNombre() %>
                                - Q<%= menu.getPrecio() %>
                            </option>

                        <%  }
                            }
                        } %>
                    </select>

                    <input type="number"
                           name="cantidad"
                           min="1"
                           value="1"
                           required>

                    <button type="button"
                            class="eliminar"
                            onclick="eliminarDetalle(this)">
                        Quitar
                    </button>

                </div>

            </div>

            <button type="button"
                    class="agregar"
                    onclick="agregarDetalle()">
                + Agregar otro artículo
            </button>

            <br>

            <button type="submit" class="guardar">
                Registrar pedido
            </button>

        </form>

    <% } %>

    <br>

    <a class="volver"
       href="<%= request.getContextPath() %>/pedidos">
        Volver a pedidos
    </a>

</div>

<script>
    function agregarDetalle() {
        const detalles = document.getElementById("detalles");
        const original = detalles.querySelector(".detalle");
        const nuevo = original.cloneNode(true);

        nuevo.querySelector("select").selectedIndex = 0;
        nuevo.querySelector("input").value = 1;

        detalles.appendChild(nuevo);
    }

    function eliminarDetalle(boton) {
        const detalles = document.getElementById("detalles");

        if (detalles.querySelectorAll(".detalle").length > 1) {
            boton.closest(".detalle").remove();
        } else {
            alert("El pedido debe tener al menos un artículo.");
        }
    }
</script>

</body>
</html>
