-- Remover o banco de dados existente, se houver
DROP DATABASE IF EXISTS springboot;

-- Criar o banco de dados
CREATE DATABASE IF NOT EXISTS springboot;

-- Selecionar o banco de dados para uso
USE springboot;

-- Criar a tabela 'roles'
CREATE TABLE roles
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Criar a tabela 'users'
CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    full_name  VARCHAR(100) NOT NULL,
    mobile     VARCHAR(15)  NOT NULL,
    role_id    BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Chave estrangeira para a tabela 'roles'
    CONSTRAINT fk_users_roles FOREIGN KEY (role_id) REFERENCES roles (id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Criar a tabela 'address'
CREATE TABLE address
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    address_line VARCHAR(100) NOT NULL,
    city         VARCHAR(15)  NOT NULL,
    state        VARCHAR(15)  NOT NULL,
    country      VARCHAR(15)  NOT NULL,
    pin_code     VARCHAR(6)   NOT NULL,
    user_id      BIGINT,

    -- Chave estrangeira para a tabela 'users'
    CONSTRAINT fk_address_users FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- (Opcional) Criar índices para melhorar o desempenho das consultas
CREATE INDEX idx_users_role_id ON users (role_id);
CREATE INDEX idx_address_user_id ON address (user_id);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_full_name ON users (full_name);
CREATE INDEX idx_address_city ON address (city);
CREATE INDEX idx_address_state ON address (state);
CREATE INDEX idx_address_country ON address (country);
CREATE INDEX idx_address_pin_code ON address (pin_code);


-- Inserir papéis
INSERT INTO roles (name)
VALUES ('ADMIN'),
       ('USER');

-- Inserir usuários
insert into users (username, password, email, full_name, mobile, role_id)
values ('Tiago Lopes', '$2a$10$XBQ9jnH3tqdUSqeTRfvrQOFyZsqxPym29nGKrlyhYUUYU7jg9dvMC', 'tlopes1@gmail.com',
        'Tiago Lopes', '1234567890', 1),
       ( 'Tiago Santos', '$2a$10$XBQ9jnH3tqdUSqeTRfvrQOFyZsqxPym29nGKrlyhYUUYU7jg9dvMC', 'tlopes2@gmail.com'
       , 'Tiago Santos', '1234567890'
       , 1);

INSERT INTO users (username, password, email, full_name, mobile, role_id)
VALUES ('john_doe', 'password123', 'john.doe@example.com', 'John Doe', '1234567890', 1),
       ('jane_smith', 'securePass456', 'jane.smith@example.com', 'Jane Smith', '0987654321', 2);

-- Inserir endereços
INSERT INTO address (address_line, city, state, country, pin_code, user_id)
VALUES ('123 Main St', 'Springfield', 'Illinois', 'USA', '62704', 1),
       ('456 Elm St', 'Shelbyville', 'Illinois', 'USA', '62565', 2);
