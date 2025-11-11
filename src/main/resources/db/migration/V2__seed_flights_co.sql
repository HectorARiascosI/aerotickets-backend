-- V2: Semillas de vuelos domésticos Colombia (fechas relativas a hoy)
-- Nota: Render ejecuta Flyway al arrancar. Mantén horarios realistas.

-- Borra duplicados si ya existen mismos (airline, origin, destination, departure_at)
DELETE FROM flights f
USING flights g
WHERE f.id > g.id
  AND f.airline = g.airline
  AND f.origin = g.origin
  AND f.destination = g.destination
  AND f.departure_at = g.departure_at;

-- Función auxiliar para crear timestamps a partir de hoy
-- (Si no te deja crear funciones por permisos, puedes inlinear NOW() + interval)
-- Aquí lo haremos directo con NOW() y fechas/hours fijos.

-- Bogotá – Medellín (Avianca)
INSERT INTO flights(airline, origin, destination, departure_at, arrive_at, total_seats, price, version, created_at)
VALUES
('Avianca','BOG','MDE', date_trunc('day', now()) + interval '10 hour', date_trunc('day', now()) + interval '11 hour 10 min', 180, 220000, 0, now()),
('Avianca','MDE','BOG', date_trunc('day', now()) + interval '13 hour', date_trunc('day', now()) + interval '14 hour 10 min', 180, 230000, 0, now())
ON CONFLICT DO NOTHING;

-- Bogotá – Cartagena (LATAM)
INSERT INTO flights(airline, origin, destination, departure_at, arrive_at, total_seats, price, version, created_at)
VALUES
('LATAM Colombia','BOG','CTG', date_trunc('day', now()) + interval '09 hour 30 min', date_trunc('day', now()) + interval '10 hour 55 min', 180, 260000, 0, now()),
('LATAM Colombia','CTG','BOG', date_trunc('day', now()) + interval '12 hour 30 min', date_trunc('day', now()) + interval '13 hour 55 min', 180, 255000, 0, now())
ON CONFLICT DO NOTHING;

-- Bogotá – Barranquilla (Wingo)
INSERT INTO flights(airline, origin, destination, departure_at, arrive_at, total_seats, price, version, created_at)
VALUES
('Wingo','BOG','BAQ', date_trunc('day', now()) + interval '15 hour', date_trunc('day', now()) + interval '16 hour 25 min', 186, 240000, 0, now()),
('Wingo','BAQ','BOG', date_trunc('day', now()) + interval '17 hour 30 min', date_trunc('day', now()) + interval '18 hour 55 min', 186, 245000, 0, now())
ON CONFLICT DO NOTHING;

-- Bogotá – San Andrés (Avianca)
INSERT INTO flights(airline, origin, destination, departure_at, arrive_at, total_seats, price, version, created_at)
VALUES
('Avianca','BOG','ADZ', date_trunc('day', now()) + interval '07 hour', date_trunc('day', now()) + interval '08 hour 50 min', 180, 420000, 0, now()),
('Avianca','ADZ','BOG', date_trunc('day', now()) + interval '12 hour', date_trunc('day', now()) + interval '13 hour 50 min', 180, 430000, 0, now())
ON CONFLICT DO NOTHING;

-- Regionales (Satena / Clic)
INSERT INTO flights(airline, origin, destination, departure_at, arrive_at, total_seats, price, version, created_at)
VALUES
('Satena','BOG','LET', date_trunc('day', now()) + interval '06 hour 30 min', date_trunc('day', now()) + interval '08 hour 30 min', 48, 520000, 0, now()),
('Satena','LET','BOG', date_trunc('day', now()) + interval '14 hour', date_trunc('day', now()) + interval '16 hour', 48, 525000, 0, now()),
('Clic','MDE','PEI', date_trunc('day', now()) + interval '11 hour', date_trunc('day', now()) + interval '11 hour 50 min', 70, 190000, 0, now()),
('Clic','PEI','MDE', date_trunc('day', now()) + interval '17 hour 20 min', date_trunc('day', now()) + interval '18 hour 10 min', 70, 195000, 0, now())
ON CONFLICT DO NOTHING;