-- Agregar campos para rastrear el estado de pago de las reservas

-- Paso 1: Agregar columna paid como nullable primero
ALTER TABLE reservations ADD COLUMN IF NOT EXISTS paid BOOLEAN;

-- Paso 2: Actualizar todos los registros existentes a false
UPDATE reservations SET paid = false WHERE paid IS NULL;

-- Paso 3: Ahora hacer la columna NOT NULL con default
ALTER TABLE reservations ALTER COLUMN paid SET NOT NULL;
ALTER TABLE reservations ALTER COLUMN paid SET DEFAULT false;

-- Paso 4: Agregar columna paid_at (nullable, se llena cuando se paga)
ALTER TABLE reservations ADD COLUMN IF NOT EXISTS paid_at TIMESTAMP;

-- Paso 5: Crear índice para búsquedas por estado de pago (solo si no existe)
CREATE INDEX IF NOT EXISTS idx_res_paid ON reservations(paid);
