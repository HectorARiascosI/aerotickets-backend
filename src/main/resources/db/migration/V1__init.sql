-- Aerotickets - V1 initial schema (PostgreSQL)
-- Tables: users, flights, reservations
-- Indexes and key constraints for searches and consistency

-- Users table
CREATE TABLE IF NOT EXISTS users (
  id           BIGSERIAL PRIMARY KEY,
  name         VARCHAR(120)        NOT NULL,
  email        VARCHAR(190)        NOT NULL,
  password     VARCHAR(250)        NOT NULL,
  role         VARCHAR(30)         NOT NULL DEFAULT 'USER',
  enabled      BOOLEAN             NOT NULL DEFAULT TRUE,
  created_at   TIMESTAMP           NOT NULL DEFAULT NOW(),
  updated_at   TIMESTAMP           NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_users_email ON users (email);

-- Flights table (aligned with com.aerotickets.entity.Flight)
CREATE TABLE IF NOT EXISTS flights (
  id            BIGSERIAL PRIMARY KEY,
  airline       VARCHAR(120)       NOT NULL,
  origin        VARCHAR(10)        NOT NULL,
  destination   VARCHAR(10)        NOT NULL,
  departure_at  TIMESTAMP          NOT NULL,
  arrive_at     TIMESTAMP          NOT NULL,
  total_seats   INTEGER            NOT NULL CHECK (total_seats >= 0),
  price         NUMERIC(12,2)      NOT NULL CHECK (price >= 0),
  version       INTEGER            NOT NULL DEFAULT 0,
  created_at    TIMESTAMP          NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_flights_unique_dep
  ON flights (airline, origin, destination, departure_at);

CREATE INDEX IF NOT EXISTS idx_flights_route_departure
  ON flights (origin, destination, departure_at);

-- Reservations table
CREATE TABLE IF NOT EXISTS reservations (
  id           BIGSERIAL PRIMARY KEY,
  user_id      BIGINT              NOT NULL REFERENCES users(id)   ON DELETE CASCADE,
  flight_id    BIGINT              NOT NULL REFERENCES flights(id) ON DELETE CASCADE,
  seat_number  INTEGER,
  seats        INTEGER             NOT NULL DEFAULT 1 CHECK (seats >= 1),
  status       VARCHAR(20)         NOT NULL DEFAULT 'ACTIVE',
  created_at   TIMESTAMP           NOT NULL DEFAULT NOW(),
  version      INTEGER             NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_reservations_flight_seat
  ON reservations (flight_id, seat_number)
  WHERE seat_number IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_reservations_user_created
  ON reservations (user_id, created_at DESC);
