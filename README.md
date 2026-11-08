# Pan, Puntos y Premios

Proyecto universitario: sistema web de pedidos y fidelizacion de puntos para un restaurante de sandwiches. Java con Spring Boot (patron MVC), vistas en HTML con Thymeleaf y Bootstrap, MySQL y JDBC.

## Requisitos

- Java 17
- Maven 3.8+
- MySQL 8

## Base de datos

Desde la raiz del proyecto:

```bash
mysql -u root -p < db/schema.sql
mysql -u root -p < db/datos.sql
```

El primer script crea la base `pan_puntos` con sus tablas (borra la base si ya existia) y el segundo carga datos de prueba.

## Conexion

`ConexionDB` lee estas variables de entorno. Si no existen usa los valores por defecto:

| Variable | Por defecto |
|----------|-------------|
| DB_URL | jdbc:mysql://localhost:3306/pan_puntos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC |
| DB_USER | root |
| DB_PASSWORD | root |

La clave de cada quien se configura en su maquina, no en el codigo.

## Ejecutar

```bash
mvn spring-boot:run
```

La aplicacion queda en `http://localhost:8080/pan-puntos/`.

## Estructura

```
src/main/java/com/umg/edu/mvcweb
    controller    controladores (@Controller)
    form          objetos para enlazar formularios
    servicio      logica de negocio
    dao           interfaces de acceso a datos
    dao/impl      implementaciones JDBC con PreparedStatement
    modelo        entidades y enumeraciones
    excepcion     excepciones de negocio
    util          conexion a la base de datos
src/main/resources/templates     vistas HTML (Thymeleaf)
src/main/resources/static        estilos propios
db                               scripts SQL
app                              aplicacion Android
```

## Como migrar un modulo

Productos, inventario y pedidos ya estan migrados y sirven de ejemplo (`ProductoController`, `PedidoController` y `templates/productos`, `templates/pedidos`).

1. Crear `XController` en `controller` con `@Controller` y recibir el servicio con `@Autowired`.
2. Cada metodo devuelve el nombre de la vista (`"clientes/lista"`) o `"redirect:/clientes"`.
3. Crear las plantillas en `templates/<modulo>` usando los fragmentos de `fragmentos/layout.html` (`cabecera`, `menu`, `mensajes` y `scripts`).
4. Para avisar al usuario despues de guardar, usar `RedirectAttributes.addFlashAttribute("mensaje", ...)`.
5. Los errores de negocio y de base de datos los atiende `ManejadorErrores`; solo hay que dejar pasar las excepciones.

Pendientes de migrar: clientes, pagos, recompensas y reportes. El boton de pagar de la lista de pedidos ya apunta a `/pagos?id=<id del pedido>`. Los servicios, DAO y modelos de esos modulos ya funcionan y estan registrados como beans, solo falta su controlador y sus vistas.

## API para Android

`GET /pan-puntos/api/puntos?documento=XXXX` responde `{"documento", "nombre", "puntos"}`.

## Ramas

| Rama | Modulo |
|------|--------|
| feature/productos-inventario | Productos e inventario |
| feature/clientes-fidelizacion | Clientes y fidelizacion |
| feature/pedidos-pagos | Pedidos y pagos |
| feature/reportes-android | Reportes y Android |

## Convencion de commits

`tipo(modulo): descripcion`, con tipo `add`, `update`, `fix`, `delete`, `refactor` o `merge`.
