-- Insertar usuario
INSERT INTO users (id, email, name, password, role) VALUES
(1, 'test@example.com', 'Test User', 'password', 'ROLE_USER');

-- Insertar solicitud
INSERT INTO requests (id, title, description, category, status, created_at, user_id) VALUES
(1, 'Ayuda urgente', 'Necesito comida', 'Emergencia', 'PENDIENTE', CURRENT_TIMESTAMP, 1);