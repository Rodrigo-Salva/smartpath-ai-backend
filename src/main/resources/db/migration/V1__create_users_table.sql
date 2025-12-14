CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(150) NOT NULL,
                       experience_level VARCHAR(50) NOT NULL,
                       target_role VARCHAR(100),
                       is_active BOOLEAN DEFAULT TRUE,
                       email_verified BOOLEAN DEFAULT FALSE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índice para búsquedas por email
CREATE INDEX idx_users_email ON users(email);

-- Comentarios para documentación
COMMENT ON TABLE users IS 'Tabla de usuarios de la plataforma SmartPath AI';
COMMENT ON COLUMN users.experience_level IS 'Valores permitidos: STUDENT, JUNIOR, MID, CAREER_CHANGE';