-- Agregar campos para rastrear el estado de pago de las reservas

-- Agregar columna paid (por defecto false)
ALTER TABLE reservations ADD COLUMN paid BOOLEAN NOT NULL DEFAULT false;

-- Agregar columna paid_at (nullable, se llena cuando se paga)
ALTER TABLE reservations ADD COLUMN paid_at TIMESTAMP;

-- Crear índice para búsquedas por estado de pago
CREATE INDEX idx_res_paid ON reservations(paid);
