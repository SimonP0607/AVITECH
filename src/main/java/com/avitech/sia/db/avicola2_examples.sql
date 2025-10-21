
USE avicola2;

-- # 1. INSERCIONES (INSERT) - Para poblar la base de datos con datos de prueba

-- --- Usuarios ---
-- NOTA: La contraseña debe ser un hash BCrypt generado por la aplicación.
-- El valor 'hash_bcrypt_aqui' es un placeholder.
INSERT INTO Usuarios (usuario, password, rol) VALUES
('admin', '$2a$12$4o0.R.dD9/T4G1.O.H.U.e.C.L.j.K.p.Q.w.X.y.Z.a.B.c.D.e.F', 'ADMIN'),
('supervisor', '$2a$12$anotherhashvaluegoeshere...1234567890abcdefghijklm', 'SUPERVISOR'),
('operador', '$2a$12$andathirdoneforagoodmeasure.1234567890abcdefghijklm', 'OPERADOR');

-- --- Lotes y Galpones ---
INSERT INTO Lote (nombre_lote, estado, cantidadGallinas) VALUES
('Lote Ponedoras 2023-A', 'Activo', 5000),
('Lote Crianza 2023-B', 'En desarrollo', 3000);

-- Asignar galpones a los lotes (asumiendo que los IDs de lote son 1 y 2)
INSERT INTO Galpones (Id_lote, nombre, capacidad, estado_sanitario, id_usuario) VALUES
(1, 'Galpón 1A', 2500, 'Bueno', 2), -- Supervisor a cargo
(1, 'Galpón 1B', 2500, 'Bueno', 2),
(2, 'Galpón 2A (Crianza)', 3000, 'Excelente', 3); -- Operador a cargo

-- --- Medicamentos (Inventario de Suministros) ---
INSERT INTO Medicamentos (nombre, presentacion, stock, stock_minimo, valor_unitario) VALUES
('Vacuna Newcastle B1', 'Frasco 1000 dosis', 50, 10, 15.50),
('Antibiótico Enrofloxacina 10%', 'Botella 1L', 20, 5, 25.00),
('Vitaminas + Electrolitos', 'Sobre 1kg', 100, 20, 8.75);

-- --- Suministros (Movimientos de inventario general) ---
INSERT INTO Suministros (fecha, tipo, item, cantidad, unidad, responsable, proveedor, motivo, valor_total) VALUES
('2023-10-25', 'Entrada', 'Maíz molido', 5000, 'kg', 'operador', 'Agroinsumos del Sur', 'Compra semanal', 1500.00),
('2023-10-26', 'Salida', 'Vacuna Newcastle B1', 5, 'Frasco 1000 dosis', 'supervisor', NULL, 'Plan sanitario Lote 1', 77.50),
('2023-10-27', 'Entrada', 'Cajas de huevos', 200, 'unidades', 'operador', 'Empaques Nacionales', 'Reposición de stock', 300.00);

-- --- Producción de Huevos ---
-- Asumiendo que los IDs de galpón son 1 y 2
INSERT INTO ProduccionHuevos (fecha, galpon, total_huevos, huevos_L, huevos_M, huevos_S, mortalidad, responsable) VALUES
('2023-10-27', 1, 2250, 1000, 1200, 50, 2, 'operador'),
('2023-10-27', 2, 2180, 950, 1180, 50, 1, 'operador');

-- --- Monitoreo Ambiental ---
INSERT INTO Monitoreo (id_galpon, temperatura, humedad, fecha) VALUES
(1, 24.5, 65.2, '2023-10-28'),
(2, 24.7, 64.8, '2023-10-28');

-- --- Plan Sanitario ---
-- Asumiendo ID de lote 1 y ID de medicamento 1
INSERT INTO Plan_Sanitario (Id_lote, Id_medicamento, fecha, nombre_enfermedad, descripcion) VALUES
(1, 1, '2023-11-15', 'Newcastle', 'Vacunación de refuerzo semana 40');

-- --- Alertas ---
-- El estado 'Activa' es el default
INSERT INTO Alertas (tipo, descripcion, categoria, id_item) VALUES
('Bajo', 'Stock bajo de Antibiótico Enrofloxacina 10%', 'Medicamentos', 2), -- id_item apunta al ID del medicamento
('Advertencia', 'Temperatura elevada en Galpón 1B', 'Monitoreo', 2); -- id_item apunta al ID del galpón

-- --- Respaldos ---
-- Asumiendo que el usuario con ID 1 es 'admin'
INSERT INTO Respaldos (archivo, fecha_hora, tipo, tamaño, estado, usuario) VALUES
('backup-20231028-020000.sql.gz', '2023-10-28 02:00:00', 'Completo', '15.2 MB', 'Exitoso', 1);

-- --- Auditoría ---
-- Registra un inicio de sesión y una modificación
INSERT INTO Auditoria (id_usuario, accion, modulo, detalle, referencia) VALUES
(1, 'Login', 'Login', 'Inicio de sesión exitoso', 'IP 192.168.1.100'),
(2, 'Modificar', 'Suministros', 'Cambió el stock del item ID 2 de 25 a 20', 'item:2');


-- 2. CONSULTAS (SELECT) - Para obtener datos de la base de datos

-- --- Obtener todos los movimientos de suministros de la última semana ---
SELECT * FROM Suministros
WHERE fecha >= CURDATE() - INTERVAL 7 DAY
ORDER BY fecha DESC;

-- --- Listar medicamentos con stock por debajo del mínimo (genera alertas) ---
SELECT Id_Medicamento, nombre, stock, stock_minimo
FROM Medicamentos
WHERE stock < stock_minimo;

-- --- Calcular la producción total de huevos de un día específico ---
SELECT SUM(total_huevos) AS produccion_total_del_dia
FROM ProduccionHuevos
WHERE fecha = '2023-10-27';

-- --- Ver la producción por galpón en una fecha ---
SELECT g.nombre, p.total_huevos, p.mortalidad, p.responsable
FROM ProduccionHuevos p
JOIN Galpones g ON p.galpon = g.id_galpon
WHERE p.fecha = '2023-10-27';

-- --- Obtener el historial de auditoría de un usuario específico ---
SELECT a.fecha, a.accion, a.modulo, a.detalle
FROM Auditoria a
JOIN Usuarios u ON a.id_usuario = u.id_usuario
WHERE u.usuario = 'supervisor'
ORDER BY a.fecha DESC;

-- --- Listar todas las alertas activas ---
SELECT tipo, descripcion, categoria, fecha
FROM Alertas
WHERE estado = 'Activa'
ORDER BY fecha DESC;

-- --- Consultar el plan sanitario para un lote ---
SELECT ps.fecha, m.nombre AS medicamento, ps.nombre_enfermedad, ps.descripcion
FROM Plan_Sanitario ps
JOIN Medicamentos m ON ps.Id_medicamento = m.Id_Medicamento
WHERE ps.Id_lote = 1;

-- --- Obtener los últimos respaldos realizados ---
SELECT r.archivo, r.fecha_hora, r.tipo, r.tamaño, r.estado, u.usuario
FROM Respaldos r
JOIN Usuarios u ON r.usuario = u.id_usuario
ORDER BY r.fecha_hora DESC
LIMIT 10;


-- 3. ACTUALIZACIONES (UPDATE) - Ejemplos de modificaciones de datos

-- --- Actualizar el stock de un medicamento después de una salida ---
-- Esto normalmente se haría desde la lógica de la aplicación
UPDATE Medicamentos
SET stock = stock - 5
WHERE Id_Medicamento = 1;

-- --- Cambiar el estado de un lote de 'En desarrollo' a 'Activo' ---
UPDATE Lote
SET estado = 'Activo'
WHERE Id_lote = 2;

-- --- Marcar una alerta como 'Resuelta' ---
UPDATE Alertas
SET estado = 'Resuelta'
WHERE id_alerta = 1;

-- --- Cambiar el responsable de un galpón ---
UPDATE Galpones
SET id_usuario = 3 -- Asignar al 'operador'
WHERE id_galpon = 1;


-- 4. ELIMINACIONES (DELETE) - Para borrar registros

-- --- Borrar un registro de respaldo antiguo ---
DELETE FROM Respaldos
WHERE id_respaldo = 1;

-- --- Eliminar registros de monitoreo de más de un año de antigüedad ---
DELETE FROM Monitoreo
WHERE fecha < CURDATE() - INTERVAL 1 YEAR;

-- --- Eliminar un usuario (requiere borrar o reasignar sus dependencias primero) ---
UPDATE Galpones SET id_usuario = 1 WHERE id_usuario = 3;
UPDATE Auditoria SET id_usuario = 1 WHERE id_usuario = 3;
DELETE FROM Usuarios WHERE id_usuario = 3; -- Borra al 'operador'


-- 5. VISTAS (VIEWS) - Para simplificar consultas complejas

-- --- Vista para el inventario actual con su valorización ---
CREATE OR REPLACE VIEW Vista_Inventario_Valorizado AS
SELECT
    Id_Medicamento,
    nombre,
    presentacion,
    stock,
    stock_minimo,
    valor_unitario,
    (stock * valor_unitario) AS valor_total_stock
FROM
    Medicamentos;

-- Consulta sobre la vista:
SELECT * FROM Vista_Inventario_Valorizado;

-- --- Vista para el estado actual de los galpones con su lote y responsable ---
CREATE OR REPLACE VIEW Vista_Estado_Galpones AS
SELECT
    g.id_galpon,
    g.nombre AS nombre_galpon,
    l.nombre_lote,
    l.estado AS estado_lote,
    g.capacidad,
    g.estado_sanitario,
    u.usuario AS responsable
FROM Galpones g
JOIN Lote l ON g.Id_lote = l.Id_lote
JOIN Usuarios u ON g.id_usuario = u.id_usuario;

-- Consulta sobre la vista:
SELECT * FROM Vista_Estado_Galpones;
