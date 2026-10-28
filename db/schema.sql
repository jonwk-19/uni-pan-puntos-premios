DROP DATABASE IF EXISTS pan_puntos;
CREATE DATABASE pan_puntos CHARACTER SET utf8mb4;
USE pan_puntos;

CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    documento VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    puntos INT NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro DATE NOT NULL DEFAULT (CURRENT_DATE),
    CHECK (puntos >= 0)
);

CREATE TABLE productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    categoria ENUM('SANDWICH', 'BEBIDA', 'ACOMPANAMIENTO') NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    existencia INT NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (precio >= 0),
    CHECK (existencia >= 0)
);

CREATE TABLE menus (
    id_menu INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    id_sandwich INT NOT NULL,
    id_bebida INT NOT NULL,
    id_acompanamiento INT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (precio >= 0),
    FOREIGN KEY (id_sandwich) REFERENCES productos(id_producto),
    FOREIGN KEY (id_bebida) REFERENCES productos(id_producto),
    FOREIGN KEY (id_acompanamiento) REFERENCES productos(id_producto)
);

CREATE TABLE recompensas (
    id_recompensa INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    puntos_requeridos INT NOT NULL,
    id_producto INT,
    id_menu INT,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (puntos_requeridos > 0),
    CHECK ((id_producto IS NULL) <> (id_menu IS NULL)),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto),
    FOREIGN KEY (id_menu) REFERENCES menus(id_menu)
);

CREATE TABLE pedidos (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('PENDIENTE', 'PAGADO', 'ENTREGADO', 'ANULADO') NOT NULL DEFAULT 'PENDIENTE',
    total DECIMAL(10,2) NOT NULL DEFAULT 0,
    puntos_ganados INT NOT NULL DEFAULT 0,
    CHECK (total >= 0),
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
);

CREATE TABLE detalle_pedido (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_producto INT,
    id_menu INT,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    CHECK (cantidad > 0),
    CHECK (precio_unitario >= 0),
    CHECK ((id_producto IS NULL) <> (id_menu IS NULL)),
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto),
    FOREIGN KEY (id_menu) REFERENCES menus(id_menu)
);

CREATE TABLE pagos (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    metodo ENUM('EFECTIVO', 'TARJETA') NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    monto_recibido DECIMAL(10,2),
    cambio DECIMAL(10,2),
    estado ENUM('APROBADO', 'RECHAZADO') NOT NULL,
    referencia VARCHAR(30),
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (monto >= 0),
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
);

CREATE TABLE movimientos_inventario (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cantidad INT NOT NULL,
    tipo ENUM('ENTRADA', 'SALIDA') NOT NULL,
    id_pedido INT,
    CHECK (cantidad > 0),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto),
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
);

CREATE TABLE movimientos_puntos (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    puntos INT NOT NULL,
    tipo ENUM('ACUMULACION', 'CANJE') NOT NULL,
    id_pedido INT,
    id_recompensa INT,
    CHECK (puntos <> 0),
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido),
    FOREIGN KEY (id_recompensa) REFERENCES recompensas(id_recompensa)
);
