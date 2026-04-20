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
