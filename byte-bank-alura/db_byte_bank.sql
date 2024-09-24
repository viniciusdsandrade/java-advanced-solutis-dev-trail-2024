DROP DATABASE IF EXISTS db_byte_bank;
CREATE DATABASE IF NOT EXISTS db_byte_bank;
USE db_byte_bank;


CREATE TABLE IF NOT EXISTS cliente
(
    id    BIGINT UNSIGNED AUTO_INCREMENT,
    nome  VARCHAR(255) NOT NULL,
    cpf   VARCHAR(11)  NOT NULL,
    email VARCHAR(255) NOT NULL,

    UNIQUE (cpf),
    UNIQUE (email),

    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS conta
(
    id         BIGINT UNSIGNED AUTO_INCREMENT,
    numero     INT UNSIGNED    NOT NULL,
    saldo      DECIMAL(10, 2)  NOT NULL,
    titular_id BIGINT UNSIGNED NOT NULL,

    UNIQUE (numero),

    PRIMARY KEY (id),
    FOREIGN KEY (titular_id) REFERENCES cliente (id)
);

INSERT INTO cliente (nome, cpf, email) VALUES ('João Silva', '12345678901', 'joao.silva@email.com');
INSERT INTO cliente (nome, cpf, email) VALUES ('Maria Souza', '98765432109', 'maria.souza@email.com');
INSERT INTO cliente (nome, cpf, email) VALUES ('Pedro Santos', '55555555555', 'pedro.santos@email.com');
INSERT INTO cliente (nome, cpf, email) VALUES ('Ana Oliveira', '44444444444', 'ana.oliveira@email.com');
INSERT INTO cliente (nome, cpf, email) VALUES ('Carlos Pereira', '33333333333', 'carlos.pereira@email.com');
INSERT INTO cliente (nome, cpf, email) VALUES ('Juliana Alves', '22222222222', 'juliana.alves@email.com');
INSERT INTO cliente (nome, cpf, email) VALUES ('Ricardo Lima', '11111111111', 'ricardo.lima@email.com');
INSERT INTO cliente (nome, cpf, email) VALUES ('Fernanda Costa', '66666666666', 'fernanda.costa@email.com');
INSERT INTO cliente (nome, cpf, email) VALUES ('Lucas Rodrigues', '77777777777', 'lucas.rodrigues@email.com');

INSERT INTO conta (numero, saldo, titular_id) VALUES (1, 1000.50, 1);
INSERT INTO conta (numero, saldo, titular_id) VALUES (2, 500.00, 2);
INSERT INTO conta (numero, saldo, titular_id) VALUES (3, 0.00, 3);
INSERT INTO conta (numero, saldo, titular_id) VALUES (4, 2500.75, 4);
INSERT INTO conta (numero, saldo, titular_id) VALUES (5, 1234.56, 5);
INSERT INTO conta (numero, saldo, titular_id) VALUES (6, 800.30, 1);
INSERT INTO conta (numero, saldo, titular_id) VALUES (7, 3000.00, 6);
INSERT INTO conta (numero, saldo, titular_id) VALUES (8, 999.99, 7);
INSERT INTO conta (numero, saldo, titular_id) VALUES (9, 5555.55, 8);
INSERT INTO conta (numero, saldo, titular_id) VALUES (10, 100.00, 9);