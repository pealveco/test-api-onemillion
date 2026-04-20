CREATE TABLE IF NOT EXISTS leads (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefono VARCHAR(30),
    fuente VARCHAR(80) NOT NULL,
    producto_interes VARCHAR(120),
    presupuesto NUMERIC(14, 2),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_leads_created_at ON leads (created_at);
CREATE INDEX IF NOT EXISTS idx_leads_fuente ON leads (fuente);

-- Limpiar para evitar duplicados en reinicio si el modo es always
DELETE FROM leads;

INSERT INTO leads (nombre, email, telefono, fuente, producto_interes, presupuesto, deleted, created_at, updated_at) VALUES
('Juan Perez', 'juan.perez@example.com', '3001234567', 'FACEBOOK', 'Seguro de Vida', 1500000.00, false, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP),
('Maria Lopez', 'maria.lopez@example.com', '3109876543', 'INSTAGRAM', 'Credito Hipotecario', 2500000.50, false, CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP),
('Carlos Ruiz', 'carlos.ruiz@example.com', NULL, 'LANDING_PAGE', 'Tarjeta Platinum', NULL, false, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP),
('Ana Garcia', 'ana.garcia@example.com', '3201112233', 'REFERIDO', 'Seguro Vehicular', 800000.00, false, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP),
('Luis Torres', 'luis.torres@example.com', '3005556677', 'OTRO', 'Cuenta Ahorros', 100000.00, false, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP),
('Elena Gomez', 'elena.gomez@example.com', '3114443322', 'FACEBOOK', 'Credito Libre Inversion', 5000000.00, false, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP),
('Pedro Serna', 'pedro.serna@example.com', NULL, 'INSTAGRAM', 'Seguro de Vida', 1200000.00, false, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP),
('Sofia Castro', 'sofia.castro@example.com', '3157778899', 'LANDING_PAGE', 'Inversion Pro', 10000000.00, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Diego Marin', 'diego.marin@example.com', '3012223344', 'FACEBOOK', 'Seguro Educativo', NULL, false, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP),
('Laura Mesa', 'laura.mesa@example.com', '3126665544', 'REFERIDO', 'Microcredito', 450000.00, false, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP),
('Marta Villa', 'marta.villa@example.com', '3009990099', 'OTRO', 'CDT', 2000000.00, false, CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP),
('Lead Eliminado', 'eliminado@example.com', '000000', 'INSTAGRAM', 'Test', 100.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
