-- SQL script para la aplicación Avicola2
CREATE DATABASE IF NOT EXISTS avicola2;
USE avicola2;

CREATE TABLE IF NOT EXISTS Usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(100),
    password VARCHAR(100),
    rol VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS Lote (
    Id_lote INT AUTO_INCREMENT PRIMARY KEY,
    nombre_lote VARCHAR(25) NOT NULL,
    estado VARCHAR(45) NOT NULL,
    cantidadGallinas INT NOT NULL
);

CREATE TABLE IF NOT EXISTS Galpones (
    id_galpon INT AUTO_INCREMENT PRIMARY KEY,
    Id_lote INT NOT NULL,
    nombre VARCHAR(100),
    capacidad INT,
    estado_sanitario VARCHAR(100),
    id_usuario INT,
    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario),
    FOREIGN KEY (Id_lote) REFERENCES Lote(Id_lote)
);

CREATE TABLE IF NOT EXISTS Monitoreo (
    id_monitoreo INT AUTO_INCREMENT PRIMARY KEY,
    id_galpon INT,
    temperatura FLOAT,
    humedad FLOAT,
    fecha DATE,
    FOREIGN KEY (id_galpon) REFERENCES Galpones(id_galpon)
);

CREATE TABLE IF NOT EXISTS Medicamentos (
    Id_Medicamento INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL, 
    presentacion VARCHAR(45) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    stock_minimo INT DEFAULT 0,
    valor_unitario DECIMAL(10,2) DEFAULT 0.00
);

CREATE TABLE IF NOT EXISTS Plan_Sanitario (
    ID_planSanitario INT AUTO_INCREMENT PRIMARY KEY,
    Id_lote INT NOT NULL,
    Id_medicamento INT NOT NULL,
    fecha DATE NOT NULL,
    nombre_enfermedad VARCHAR(45) NOT NULL,
    muertes INT DEFAULT 0,
    descripcion VARCHAR(200),
    observaciones VARCHAR(200),
    FOREIGN KEY (Id_lote) REFERENCES Lote(Id_lote),
    FOREIGN KEY (Id_medicamento) REFERENCES Medicamentos(Id_Medicamento)
);

CREATE TABLE IF NOT EXISTS ProduccionHuevos (
    Id_produccion INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    galpon INT NOT NULL,
    total_huevos INT NOT NULL DEFAULT 0,
    huevos_L INT DEFAULT 0,
    huevos_M INT DEFAULT 0,
    huevos_S INT DEFAULT 0,
    temperatura FLOAT,
    humedad FLOAT,
    mortalidad INT DEFAULT 0,
    responsable VARCHAR(100),
    FOREIGN KEY (galpon) REFERENCES Galpones(id_galpon)
);

CREATE TABLE IF NOT EXISTS Suministros (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    tipo ENUM('Entrada','Salida') NOT NULL,
    item VARCHAR(100) NOT NULL,
    cantidad INT NOT NULL,
    unidad VARCHAR(50),
    responsable VARCHAR(100),
    proveedor VARCHAR(100),
    motivo VARCHAR(200),
    valor_total DECIMAL(10,2) DEFAULT 0.00
);

CREATE TABLE IF NOT EXISTS Alertas (
    id_alerta INT AUTO_INCREMENT PRIMARY KEY,
    tipo ENUM('Crítico','Bajo','Advertencia') NOT NULL,
    descripcion VARCHAR(200),
    categoria VARCHAR(50),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_item INT,
    estado ENUM('Activa','Resuelta') DEFAULT 'Activa'
);

CREATE TABLE IF NOT EXISTS Respaldos (
    id_respaldo INT AUTO_INCREMENT PRIMARY KEY,
    archivo VARCHAR(100),
    fecha_hora DATETIME,
    tipo ENUM('Completo','Selectivo'),
    tamaño VARCHAR(20),
    estado ENUM('Exitoso','Error') DEFAULT 'Exitoso',
    usuario INT,
    FOREIGN KEY (usuario) REFERENCES Usuarios(id_usuario)
);
