-- Migración para cambiar seat_number de INTEGER a VARCHAR(10)
-- Esto permite asientos con formato "1A", "2B", etc.

-- Primero, crear una columna temporal para almacenar los valores convertidos
ALTER TABLE reservations ADD COLUMN seat_number_temp VARCHAR(10);

-- Copiar los valores existentes agregando 'A' al final
UPDATE reservations 
SET seat_number_temp = CAST(seat_number AS VARCHAR) || 'A' 
WHERE seat_number IS NOT NULL;

-- Eliminar la columna antigua
ALTER TABLE reservations DROP COLUMN seat_number;

-- Renombrar la columna temporal
ALTER TABLE reservations RENAME COLUMN seat_number_temp TO seat_number;
