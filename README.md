# Pan, Puntos y Premios

Proyecto universitario: sistema web de pedidos y fidelizacion de puntos para un restaurante de sandwiches. Java con patron MVC (servlets y JSP), MySQL y JDBC.

## Requisitos

- Java 17
- Maven 3.8+
- MySQL 8
- Tomcat 10.1 (jakarta.servlet)

## Base de datos

Desde la raiz del proyecto:

```bash
mysql -u root -p < db/schema.sql
mysql -u root -p < db/datos.sql
```

El primer script crea la base `pan_puntos` con sus tablas y el segundo carga datos de prueba (clientes, productos, menus, recompensas y pedidos pagados de ejemplo).

La conexion se configura en `src/main/java/com/panpuntos/util/ConexionDB.java` (`URL`, `USUARIO` y `CLAVE`).

## Compilar y ejecutar

```bash
mvn clean package
```

Copiar `target/pan-puntos.war` a la carpeta `webapps` de Tomcat, o desplegarlo desde el IDE, y abrir `http://localhost:8080/pan-puntos/`.

## Estructura

```
src/main/java/com/panpuntos
    modelo        entidades y enumeraciones
    dao           interfaces de acceso a datos
    dao/impl      implementaciones JDBC con PreparedStatement
    servicio      logica de negocio
    controlador   servlets
    filtro        filtros web
    excepcion     excepciones de negocio
    util          conexion a la base de datos
src/main/webapp/WEB-INF/vista    paginas JSP
db                               scripts SQL
```

## Ramas

| Rama | Modulo |
|------|--------|
| feature/productos-inventario | Productos e inventario |
| feature/clientes-fidelizacion | Clientes y fidelizacion |
| feature/pedidos-pagos | Pedidos y pagos |
| feature/reportes-android | Reportes, Android y entregables |

## Convencion de commits

`tipo(modulo): descripcion`, con tipo `add`, `update`, `fix`, `delete`, `refactor` o `merge`.
