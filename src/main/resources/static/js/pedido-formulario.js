document.addEventListener("DOMContentLoaded", function () {
    var contenedor = document.getElementById("detalles");
    var botonAgregar = document.getElementById("agregar");

    if (!contenedor || !botonAgregar) {
        return;
    }

    botonAgregar.addEventListener("click", function () {
        var nuevo = contenedor.querySelector(".detalle").cloneNode(true);
        nuevo.querySelector("select").selectedIndex = 0;
        nuevo.querySelector("input").value = 1;
        contenedor.appendChild(nuevo);
    });

    contenedor.addEventListener("click", function (evento) {
        var boton = evento.target.closest(".quitar");

        if (!boton) {
            return;
        }

        if (contenedor.querySelectorAll(".detalle").length > 1) {
            boton.closest(".detalle").remove();
        } else {
            alert("El pedido debe tener al menos un articulo.");
        }
    });
});
