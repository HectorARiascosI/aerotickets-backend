CREATE TABLE IF NOT EXISTS flights (
  id BIGSERIAL PRIMARY KEY,
  airline VARCHAR(120) NOT NULL,
  origin VARCHAR(10) NOT NULL,
  destination VARCHAR(10) NOT NULL,
  departure_at TIMESTAMP NOT NULL,
  arrive_at TIMESTAMP NOT NULL,
  total_seats INT NOT NULL,
  price NUMERIC(12,2) NOT NULL,
  version INT
);