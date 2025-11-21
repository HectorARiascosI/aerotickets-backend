# Instrucciones para Reparar Flyway en Producción

## Problema
La migración V5 falló en producción porque intentó agregar una columna NOT NULL a una tabla con datos existentes. Flyway marcó esta migración como fallida.

## Solución

### Opción 1: Reparar Flyway (Recomendado)

Conectarse a la base de datos de producción y ejecutar:

```sql
-- 1. Ver el estado actual de Flyway
SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC;

-- 2. Eliminar el registro fallido de V5 (si existe)
DELETE FROM flyway_schema_history WHERE version = '5' AND success = false;

-- 3. Verificar que V6 se ejecutará
-- Flyway ejecutará automáticamente V6 en el próximo despliegue
```

### Opción 2: Reparar manualmente la base de datos

Si V5 se ejecutó parcialmente, ejecutar estos comandos para corregir:

```sql
-- Verificar si la columna paid existe
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'reservations' AND column_name IN ('paid', 'paid_at');

-- Si paid existe pero tiene NULLs, actualizarlos
UPDATE reservations SET paid = false WHERE paid IS NULL;

-- Si paid no es NOT NULL, hacerla NOT NULL
ALTER TABLE reservations ALTER COLUMN paid SET NOT NULL;
ALTER TABLE reservations ALTER COLUMN paid SET DEFAULT false;

-- Si paid_at no existe, crearla
ALTER TABLE reservations ADD COLUMN IF NOT EXISTS paid_at TIMESTAMP;

-- Crear índice si no existe
CREATE INDEX IF NOT EXISTS idx_res_paid ON reservations(paid);

-- Marcar V5 como exitosa en Flyway
UPDATE flyway_schema_history 
SET success = true 
WHERE version = '5';
```

### Opción 3: Usar comando de Flyway Repair

Si tienes acceso a la línea de comandos en Render:

```bash
# Esto marcará las migraciones fallidas como reparadas
flyway repair
```

## Verificación

Después de aplicar cualquiera de las opciones, verificar:

```sql
-- 1. Verificar estructura de la tabla
\d reservations

-- 2. Verificar que todos los registros tienen paid = false
SELECT COUNT(*) FROM reservations WHERE paid IS NULL;
-- Debe retornar 0

-- 3. Verificar estado de Flyway
SELECT version, description, success, installed_on 
FROM flyway_schema_history 
ORDER BY installed_rank DESC 
LIMIT 5;
```

## Notas

- V6 está diseñada para ser idempotente (se puede ejecutar múltiples veces sin problemas)
- V6 maneja todos los casos: columna no existe, columna existe con NULLs, columna ya está correcta
- Después de reparar, el próximo despliegue debería funcionar correctamente
