USE pan_puntos;

INSERT INTO clientes (documento, nombre, apellidos, telefono, email, puntos, activo, fecha_registro) VALUES
('1012345678', 'Carlos', 'Mendoza', '5512345601', 'carlos.mendoza@correo.com', 14, TRUE, '2026-09-14'),
('1023456789', 'Ana', 'Ramirez', '5512345602', 'ana.ramirez@correo.com', 8, TRUE, '2026-09-20'),
('1034567890', 'Luis', 'Torres', '5512345603', 'luis.torres@correo.com', 2, TRUE, '2026-10-01'),
('1045678901', 'Maria', 'Gonzalez', '5512345604', 'maria.gonzalez@correo.com', 0, TRUE, '2026-10-05'),
('1056789012', 'Jorge', 'Salazar', '5512345605', NULL, 0, FALSE, '2026-10-06');

INSERT INTO productos (codigo, categoria, nombre, precio, existencia, activo) VALUES
('SAN-001', 'SANDWICH', 'Sandwich de jamon', 45.00, 37, TRUE),
('SAN-002', 'SANDWICH', 'Sandwich de pollo', 55.00, 34, TRUE),
('SAN-003', 'SANDWICH', 'Sandwich de pavo', 52.00, 29, TRUE),
('SAN-004', 'SANDWICH', 'Sandwich vegetariano', 48.00, 24, TRUE),
('BEB-001', 'BEBIDA', 'Refresco', 25.00, 58, TRUE),
('BEB-002', 'BEBIDA', 'Jugo de naranja', 30.00, 38, TRUE),
('BEB-003', 'BEBIDA', 'Agua natural', 15.00, 80, TRUE),
('ACO-001', 'ACOMPANAMIENTO', 'Papas fritas', 28.00, 49, TRUE),
('ACO-002', 'ACOMPANAMIENTO', 'Aros de cebolla', 32.00, 5, TRUE),
('ACO-003', 'ACOMPANAMIENTO', 'Ensalada', 30.00, 30, TRUE),
('BEB-004', 'BEBIDA', 'Malteada', 38.00, 0, TRUE),
('SAN-005', 'SANDWICH', 'Sandwich de atun', 50.00, 0, FALSE);

INSERT INTO menus (codigo, nombre, precio, id_sandwich, id_bebida, id_acompanamiento, activo) VALUES
('MEN-001', 'Menu clasico', 95.00, 1, 5, 8, TRUE),
('MEN-002', 'Menu pollo', 110.00, 2, 6, 9, TRUE),
('MEN-003', 'Menu ligero', 90.00, 4, 7, 10, TRUE);

INSERT INTO recompensas (nombre, puntos_requeridos, id_producto, id_menu, activa) VALUES
('Bebida gratis', 10, 5, NULL, TRUE),
('Acompanamiento gratis', 15, 8, NULL, TRUE),
('Sandwich gratis', 30, 1, NULL, TRUE),
('Menu gratis', 50, NULL, 1, TRUE);

INSERT INTO pedidos (id_cliente, fecha, estado, total, puntos_ganados) VALUES
(1, '2026-10-20 13:05:00', 'PAGADO', 115.00, 4),
(2, '2026-10-21 14:30:00', 'PAGADO', 95.00, 8),
(1, '2026-10-22 19:10:00', 'PAGADO', 162.00, 10),
(3, '2026-10-23 12:40:00', 'ENTREGADO', 78.00, 2),
(4, '2026-10-26 18:20:00', 'PENDIENTE', 83.00, 0),
(3, '2026-10-26 18:45:00', 'ANULADO', 15.00, 0);

INSERT INTO detalle_pedido (id_pedido, id_producto, id_menu, cantidad, precio_unitario) VALUES
(1, 1, NULL, 2, 45.00),
(1, 5, NULL, 1, 25.00),
(2, NULL, 1, 1, 95.00),
(3, NULL, 2, 1, 110.00),
(3, 3, NULL, 1, 52.00),
(4, 4, NULL, 1, 48.00),
(4, 6, NULL, 1, 30.00),
(5, 2, NULL, 1, 55.00),
(5, 8, NULL, 1, 28.00),
(6, 7, NULL, 1, 15.00);

INSERT INTO pagos (id_pedido, metodo, monto, monto_recibido, cambio, estado, referencia, fecha) VALUES
(1, 'EFECTIVO', 115.00, 150.00, 35.00, 'APROBADO', NULL, '2026-10-20 13:08:00'),
(2, 'TARJETA', 95.00, NULL, NULL, 'APROBADO', 'TRJ-20261021-0001', '2026-10-21 14:33:00'),
(3, 'EFECTIVO', 162.00, 200.00, 38.00, 'APROBADO', NULL, '2026-10-22 19:14:00'),
(4, 'TARJETA', 78.00, NULL, NULL, 'APROBADO', 'TRJ-20261023-0001', '2026-10-23 12:43:00'),
(5, 'TARJETA', 83.00, NULL, NULL, 'RECHAZADO', 'TRJ-20261026-0001', '2026-10-26 18:25:00');

INSERT INTO movimientos_inventario (id_producto, fecha, cantidad, tipo, id_pedido) VALUES
(1, '2026-10-15 09:00:00', 40, 'ENTRADA', NULL),
(2, '2026-10-15 09:00:00', 35, 'ENTRADA', NULL),
(3, '2026-10-15 09:00:00', 30, 'ENTRADA', NULL),
(4, '2026-10-15 09:00:00', 25, 'ENTRADA', NULL),
(5, '2026-10-15 09:00:00', 60, 'ENTRADA', NULL),
(6, '2026-10-15 09:00:00', 40, 'ENTRADA', NULL),
(7, '2026-10-15 09:00:00', 80, 'ENTRADA', NULL),
(8, '2026-10-15 09:00:00', 50, 'ENTRADA', NULL),
(9, '2026-10-15 09:00:00', 6, 'ENTRADA', NULL),
(10, '2026-10-15 09:00:00', 30, 'ENTRADA', NULL),
(1, '2026-10-20 13:08:00', 2, 'SALIDA', 1),
(5, '2026-10-20 13:08:00', 1, 'SALIDA', 1),
(1, '2026-10-21 14:33:00', 1, 'SALIDA', 2),
(5, '2026-10-21 14:33:00', 1, 'SALIDA', 2),
(8, '2026-10-21 14:33:00', 1, 'SALIDA', 2),
(2, '2026-10-22 19:14:00', 1, 'SALIDA', 3),
(6, '2026-10-22 19:14:00', 1, 'SALIDA', 3),
(9, '2026-10-22 19:14:00', 1, 'SALIDA', 3),
(3, '2026-10-22 19:14:00', 1, 'SALIDA', 3),
(4, '2026-10-23 12:43:00', 1, 'SALIDA', 4),
(6, '2026-10-23 12:43:00', 1, 'SALIDA', 4);

INSERT INTO movimientos_puntos (id_cliente, fecha, puntos, tipo, id_pedido, id_recompensa) VALUES
(1, '2026-10-20 13:08:00', 4, 'ACUMULACION', 1, NULL),
(2, '2026-10-21 14:33:00', 8, 'ACUMULACION', 2, NULL),
(1, '2026-10-22 19:14:00', 10, 'ACUMULACION', 3, NULL),
(3, '2026-10-23 12:43:00', 2, 'ACUMULACION', 4, NULL);
