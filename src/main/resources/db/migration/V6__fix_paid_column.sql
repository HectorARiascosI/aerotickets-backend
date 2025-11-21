-- Migración de corrección para el campo paid
-- Esta migración maneja el caso donde V5 pudo haber fallado parcialmente

-- Verificar si la columna existe y agregarla si no
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='reservations' AND column_name='paid') THEN
        ALTER TABLE reservations ADD COLUMN paid BOOLEAN;
    END IF;
END $$;

-- Actualizar todos los registros NULL a false
UPDATE reservations SET paid = false WHERE paid IS NULL;

-- Hacer la columna NOT NULL si aún no lo es
DO $$ 
BEGIN
    ALTER TABLE reservations ALTER COLUMN paid SET NOT NULL;
    ALTER TABLE reservations ALTER COLUMN paid SET DEFAULT false;
EXCEPTION
    WHEN OTHERS THEN
        -- Si ya es NOT NULL, ignorar el error
        NULL;
END $$;

-- Verificar si paid_at existe y agregarla si no
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='reservations' AND column_name='paid_at') THEN
        ALTER TABLE reservations ADD COLUMN paid_at TIMESTAMP;
    END IF;
END $$;

-- Crear índice si no existe
CREATE INDEX IF NOT EXISTS idx_res_paid ON reservations(paid);
