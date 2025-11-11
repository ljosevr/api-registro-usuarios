-- =====================================================
-- Script de creación de Base de Datos para H2
-- Proyecto: Registro de Usuarios - API REST
-- Arquitectura: Hexagonal + DDD
-- =====================================================

-- H2 crea la base de datos automáticamente cuando se conecta
-- Este script solo crea las tablas

-- =====================================================
-- Tabla: USERS
-- Descripción: Almacena la información de los usuarios registrados
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NOT NULL,
    token VARCHAR(500) NOT NULL,
    isactive BOOLEAN NOT NULL DEFAULT TRUE
);

-- =====================================================
-- Tabla: PHONES
-- Descripción: Almacena los teléfonos asociados a cada usuario
-- Relación: Many-to-One con users
-- =====================================================
CREATE TABLE IF NOT EXISTS phones (
    id VARCHAR(36) PRIMARY KEY,
    number VARCHAR(255) NOT NULL,
    citycode VARCHAR(255) NOT NULL,
    contrycode VARCHAR(255) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    CONSTRAINT fk_phone_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- Índices para mejorar el rendimiento
-- =====================================================
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_phones_user_id ON phones(user_id);

-- =====================================================
-- Comentarios en las tablas (H2 soporta comentarios)
-- =====================================================
COMMENT ON TABLE users IS 'Tabla principal de usuarios del sistema';
COMMENT ON TABLE phones IS 'Tabla de teléfonos asociados a usuarios';

COMMENT ON COLUMN users.id IS 'Identificador único UUID del usuario';
COMMENT ON COLUMN users.email IS 'Email único del usuario (usado para login)';
COMMENT ON COLUMN users.token IS 'Token JWT para autenticación';
COMMENT ON COLUMN users.isactive IS 'Indica si el usuario está activo en el sistema';
COMMENT ON COLUMN phones.user_id IS 'Referencia al usuario propietario del teléfono';

