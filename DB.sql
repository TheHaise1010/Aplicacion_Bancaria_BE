CREATE DATABASE banco_db;

USE banco_db;

-- Creamos la tabla cliente con las columnas solicitadas
CREATE TABLE cliente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dui VARCHAR(10)     NOT NULL UNIQUE,
    primer_nombre VARCHAR(12) NOT NULL,
    apellido       VARCHAR(12) NOT NULL,
    fecha_nacimiento DATE NOT NULL
);

CREATE TABLE cuenta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(20) NOT NULL,
    saldo DOUBLE NOT NULL,
    cliente_id BIGINT,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

-- Inserta un cliente con DUI '12345678-9'
INSERT INTO cliente (dui, primer_nombre, apellido, fecha_nacimiento)
VALUES ('12345678-9', 'María', 'López', '1992-11-05');

-- Supongamos que el ID asignado al cliente es 1, inserta dos cuentas para ese cliente:
INSERT INTO cuenta (numero, saldo, cliente_id) VALUES ('0001', 1000, 1);
INSERT INTO cuenta (numero, saldo, cliente_id) VALUES ('0002', 500, 1);

CREATE TABLE credenciales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    correo VARCHAR(50) NOT NULL,
    contrasena VARCHAR(10) NOT NULL,
    tipo_cuenta ENUM('cliente','empleado') NOT NULL,
    cliente_dui VARCHAR(10),
    -- si es cliente, cliente_dui NO puede ser NULL; si es empleado, cliente_dui deberá quedar en NULL
    CHECK (tipo_cuenta <> 'cliente' OR cliente_dui IS NOT NULL),
    FOREIGN KEY (cliente_dui) REFERENCES cliente(dui)
);