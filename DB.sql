CREATE DATABASE banco_db;

USE banco_db;

CREATE TABLE cliente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dui VARCHAR(10) UNIQUE NOT NULL
);

CREATE TABLE cuenta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(20) NOT NULL,
    saldo DOUBLE NOT NULL,
    cliente_id BIGINT,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

-- Inserta un cliente con DUI '12345678-9'
INSERT INTO cliente (dui) VALUES ('12345678-9');

-- Supongamos que el ID asignado al cliente es 1, inserta dos cuentas para ese cliente:
INSERT INTO cuenta (numero, saldo, cliente_id) VALUES ('0001', 1000, 1);
INSERT INTO cuenta (numero, saldo, cliente_id) VALUES ('0002', 500, 1);
