# Cambios Implementados en el Sistema de Reservas

## Resumen de Cambios

Se han implementado dos mejoras importantes en el sistema de reservas de AeroTickets:

### 1. Prevención de Compras Duplicadas del Mismo Vuelo
**Problema:** Los usuarios podían comprar el mismo vuelo múltiples veces después de realizar el pago.

**Solución:** Se modificó la lógica de validación en `ReservationService.java` para verificar que un usuario no pueda tener más de una reserva ACTIVA para el mismo vuelo. Esto evita que después de pagar, el usuario pueda volver a comprar el mismo vuelo.

**Archivos modificados:**
- `src/main/java/com/aerotickets/service/ReservationService.java`
- `src/main/java/com/aerotickets/constants/ErrorMessages.java`

### 2. Selección de Asientos con Identificación Alfanumérica
**Problema:** El sistema solo permitía asientos con números enteros (1, 2, 3...), pero el frontend mostraba asientos con formato "1A", "2B", etc. El backend siempre asignaba el asiento "1A" por defecto.

**Solución:** Se cambió el tipo de dato de `seatNumber` de `Integer` a `String` en toda la aplicación para soportar identificadores alfanuméricos como "1A", "2B", "12C", etc.

**Archivos modificados:**

**Backend:**
- `src/main/java/com/aerotickets/entity/Reservation.java`
- `src/main/java/com/aerotickets/dto/ReservationRequestDTO.java`
- `src/main/java/com/aerotickets/dto/ReservationResponseDTO.java`
- `src/main/java/com/aerotickets/repository/ReservationRepository.java`
- `src/main/java/com/aerotickets/service/ReservationService.java`
- `src/main/java/com/aerotickets/controller/ReservationController.java`
- `src/main/resources/db/migration/V4__change_seat_number_to_varchar.sql` (nueva migración)

**Frontend:**
- `src/components/SeatSelector.tsx`
- `src/components/FlightCard.tsx`
- `src/services/reservationService.ts`
- `src/types/index.ts`
- `src/pages/MyReservationsPage.tsx`
- `src/api/endpoints.ts`

## Nuevas Funcionalidades

### Endpoint para Obtener Asientos Ocupados
Se agregó un nuevo endpoint en el backend para obtener la lista de asientos ocupados de un vuelo específico:

```
GET /api/reservations/occupied-seats/{flightId}
```

Este endpoint retorna un array de strings con los identificadores de los asientos ocupados (ej: ["1A", "2B", "5C"]).

### Validación de Formato de Asientos
El backend ahora valida que los asientos tengan el formato correcto (número + letra mayúscula):
- ✅ Válidos: "1A", "12B", "20F"
- ❌ Inválidos: "A1", "1", "12", "1a"

### Asignación Automática de Asientos
Cuando no se especifica un asiento, el sistema asigna automáticamente el primer asiento disponible siguiendo el patrón:
- 6 asientos por fila (A, B, C, D, E, F)
- Formato: fila + letra (1A, 1B, 1C, 1D, 1E, 1F, 2A, 2B, ...)

## Migración de Base de Datos

### Instrucciones para Aplicar la Migración

La migración se aplicará automáticamente al iniciar la aplicación gracias a Flyway. El script `V4__change_seat_number_to_varchar.sql` realizará los siguientes cambios:

1. Creará una columna temporal `seat_number_temp` de tipo VARCHAR(10)
2. Copiará los valores existentes de `seat_number` agregando la letra 'A' al final
3. Eliminará la columna antigua `seat_number`
4. Renombrará `seat_number_temp` a `seat_number`

**Ejemplo de conversión:**
- Antes: `seat_number = 1` (INTEGER)
- Después: `seat_number = '1A'` (VARCHAR)

### Verificación Post-Migración

Después de ejecutar la migración, puedes verificar que los datos se migraron correctamente con:

```sql
SELECT id, seat_number, status FROM reservations LIMIT 10;
```

Deberías ver asientos con formato "1A", "2A", etc.

## Pruebas Recomendadas

### 1. Prueba de Compra Duplicada
1. Iniciar sesión con un usuario
2. Buscar un vuelo
3. Reservar y pagar el vuelo
4. Intentar reservar el mismo vuelo nuevamente
5. **Resultado esperado:** El sistema debe mostrar un error indicando que ya tienes una reserva activa para ese vuelo

### 2. Prueba de Selección de Asientos
1. Iniciar sesión con un usuario
2. Buscar un vuelo
3. Hacer clic en "Reservar"
4. Observar el selector de asientos con la distribución visual
5. Seleccionar un asiento específico (ej: "5C")
6. Confirmar la reserva
7. **Resultado esperado:** La reserva debe crearse con el asiento seleccionado ("5C")

### 3. Prueba de Asientos Ocupados
1. Con un usuario, reservar el asiento "3B" de un vuelo
2. Con otro usuario, intentar reservar el mismo vuelo
3. Abrir el selector de asientos
4. **Resultado esperado:** El asiento "3B" debe aparecer como ocupado (gris) y no seleccionable

### 4. Prueba de Asignación Automática
1. Crear una reserva sin especificar asiento (dejar en null)
2. **Resultado esperado:** El sistema debe asignar automáticamente el primer asiento disponible (ej: "1A")

## Notas Importantes

- **Compatibilidad hacia atrás:** Los datos existentes en la base de datos se migrarán automáticamente agregando la letra 'A' a los números de asiento existentes.
- **Formato de asientos:** El sistema ahora espera asientos en formato "número + letra" (ej: "1A", "12B").
- **Validación:** El backend valida el formato de los asientos antes de crear la reserva.
- **Asientos ocupados:** El frontend ahora consulta al backend para obtener la lista real de asientos ocupados en lugar de generarlos aleatoriamente.

## Rollback (en caso de problemas)

Si necesitas revertir los cambios de la base de datos, puedes ejecutar:

```sql
-- Crear columna temporal de tipo INTEGER
ALTER TABLE reservations ADD COLUMN seat_number_temp INTEGER;

-- Extraer solo el número del asiento (eliminar la letra)
UPDATE reservations 
SET seat_number_temp = CAST(REGEXP_REPLACE(seat_number, '[A-Z]', '', 'g') AS INTEGER)
WHERE seat_number IS NOT NULL;

-- Eliminar columna antigua
ALTER TABLE reservations DROP COLUMN seat_number;

-- Renombrar columna temporal
ALTER TABLE reservations RENAME COLUMN seat_number_temp TO seat_number;
```

**Nota:** Este rollback causará pérdida de información sobre la letra del asiento (A, B, C, etc.).
