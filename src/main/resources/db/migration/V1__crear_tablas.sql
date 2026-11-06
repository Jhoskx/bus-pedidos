-- Tabla de AUDITORÍA: guarda una copia de cada pedido recibido (vía Wire Tap)
CREATE TABLE IF NOT EXISTS auditoria (
    id              SERIAL PRIMARY KEY,
    correlation_id  VARCHAR(100) NOT NULL,
    trace_id        VARCHAR(100),
    contenido       TEXT,
    fecha           TIMESTAMP DEFAULT NOW()
);

-- Tabla de PEDIDOS FALLIDOS: el "buzón de devoluciones" del DLC
CREATE TABLE IF NOT EXISTS pedidos_fallidos (
    id              SERIAL PRIMARY KEY,
    correlation_id  VARCHAR(100) NOT NULL,
    contenido       TEXT,
    motivo          TEXT,
    estado          VARCHAR(20) DEFAULT 'PENDIENTE',
    fecha           TIMESTAMP DEFAULT NOW()
);