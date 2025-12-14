-- Tabla: career_paths
CREATE TABLE career_paths (
                              id BIGSERIAL PRIMARY KEY,
                              title VARCHAR(150) NOT NULL,
                              description TEXT,
                              target_role VARCHAR(100) NOT NULL,
                              difficulty_level VARCHAR(50) NOT NULL,
                              estimated_duration_weeks INTEGER,
                              is_active BOOLEAN DEFAULT TRUE,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla: career_path_skills
CREATE TABLE career_path_skills (
                                    career_path_id BIGINT NOT NULL,
                                    skill VARCHAR(255),
                                    FOREIGN KEY (career_path_id) REFERENCES career_paths(id) ON DELETE CASCADE
);

-- Tabla: career_path_prerequisites
CREATE TABLE career_path_prerequisites (
                                           career_path_id BIGINT NOT NULL,
                                           prerequisite VARCHAR(255),
                                           FOREIGN KEY (career_path_id) REFERENCES career_paths(id) ON DELETE CASCADE
);

-- Tabla: learning_resources
CREATE TABLE learning_resources (
                                    id BIGSERIAL PRIMARY KEY,
                                    title VARCHAR(200) NOT NULL,
                                    description TEXT,
                                    resource_type VARCHAR(50) NOT NULL,
                                    provider VARCHAR(100),
                                    url TEXT NOT NULL,
                                    difficulty_level VARCHAR(50),
                                    estimated_hours INTEGER,
                                    is_free BOOLEAN DEFAULT FALSE,
                                    price VARCHAR(10),
                                    rating VARCHAR(10),
                                    is_active BOOLEAN DEFAULT TRUE,
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla: resource_tags
CREATE TABLE resource_tags (
                               resource_id BIGINT NOT NULL,
                               tag VARCHAR(255),
                               FOREIGN KEY (resource_id) REFERENCES learning_resources(id) ON DELETE CASCADE
);

-- Tabla: user_progress
CREATE TABLE user_progress (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               career_path_id BIGINT,
                               resource_id BIGINT,
                               progress_percentage INTEGER NOT NULL DEFAULT 0,
                               status VARCHAR(50) NOT NULL,
                               started_at TIMESTAMP,
                               completed_at TIMESTAMP,
                               last_accessed_at TIMESTAMP,
                               notes TEXT,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                               FOREIGN KEY (career_path_id) REFERENCES career_paths(id) ON DELETE SET NULL,
                               FOREIGN KEY (resource_id) REFERENCES learning_resources(id) ON DELETE SET NULL
);

-- Tabla: ai_recommendations
CREATE TABLE ai_recommendations (
                                    id BIGSERIAL PRIMARY KEY,
                                    user_id BIGINT NOT NULL,
                                    resource_id BIGINT NOT NULL,
                                    recommendation_score DOUBLE PRECISION NOT NULL,
                                    recommendation_reason TEXT,
                                    ai_model VARCHAR(50),
                                    is_accepted BOOLEAN,
                                    user_feedback TEXT,
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                    FOREIGN KEY (resource_id) REFERENCES learning_resources(id) ON DELETE CASCADE
);

-- Índices para mejorar performance
CREATE INDEX idx_user_progress_user_id ON user_progress(user_id);
CREATE INDEX idx_user_progress_status ON user_progress(status);
CREATE INDEX idx_ai_recommendations_user_id ON ai_recommendations(user_id);
CREATE INDEX idx_learning_resources_type ON learning_resources(resource_type);
CREATE INDEX idx_career_paths_role ON career_paths(target_role);