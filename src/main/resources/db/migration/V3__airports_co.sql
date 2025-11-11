-- V3: Tabla y data de aeropuertos Colombia
CREATE TABLE IF NOT EXISTS airports_co (
  iata            VARCHAR(3) PRIMARY KEY,
  icao            VARCHAR(4),
  city            VARCHAR(80) NOT NULL,
  name            VARCHAR(120) NOT NULL,
  state           VARCHAR(60),
  latitude        NUMERIC(10,6),
  longitude       NUMERIC(10,6),
  elevation_ft    INTEGER,
  runway_len_m    INTEGER,
  terrain         VARCHAR(60),
  allowed_families TEXT NOT NULL -- lista separada por coma (p.ej. 'A320-200,A320neo,B737-800')
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_airports_co_icao ON airports_co(icao);
CREATE INDEX IF NOT EXISTS idx_airports_co_city ON airports_co(city);

-- Upsert helper (por si re-corres)
CREATE OR REPLACE FUNCTION upsert_airport_co(
    _iata VARCHAR, _icao VARCHAR, _city VARCHAR, _name VARCHAR, _state VARCHAR,
    _lat NUMERIC, _lon NUMERIC, _elev INTEGER, _rwy INTEGER, _terrain VARCHAR, _families TEXT
) RETURNS VOID AS $$
BEGIN
  INSERT INTO airports_co(iata, icao, city, name, state, latitude, longitude, elevation_ft, runway_len_m, terrain, allowed_families)
  VALUES(_iata,_icao,_city,_name,_state,_lat,_lon,_elev,_rwy,_terrain,_families)
  ON CONFLICT (iata) DO UPDATE SET
    icao = EXCLUDED.icao,
    city = EXCLUDED.city,
    name = EXCLUDED.name,
    state = EXCLUDED.state,
    latitude = EXCLUDED.latitude,
    longitude = EXCLUDED.longitude,
    elevation_ft = EXCLUDED.elevation_ft,
    runway_len_m = EXCLUDED.runway_len_m,
    terrain = EXCLUDED.terrain,
    allowed_families = EXCLUDED.allowed_families;
END;
$$ LANGUAGE plpgsql;

-- Principales y regionales (coinciden con AirportCatalogCO)
SELECT upsert_airport_co('BOG','SKBO','Bogotá','El Dorado','Cundinamarca',4.701600,-74.146900,8361,3800,'Llano/Altiplano','A320-200,A320neo,A319,A321,B737-800,B787,A330');
SELECT upsert_airport_co('MDE','SKRG','Rionegro (Medellín)','José María Córdova','Antioquia',6.164500,-75.423100,6954,3500,'Montañoso','A320-200,A320neo,A319,A321,B737-800');
SELECT upsert_airport_co('EOH','SKMD','Medellín','Olaya Herrera','Antioquia',6.219000,-75.589000,4949,2500,'Urbano','ATR 72-600,ATR 42-600,ERJ-145');
SELECT upsert_airport_co('CTG','SKCG','Cartagena','Rafael Núñez','Bolívar',10.442400,-75.513000,6,2600,'Costero','A320-200,A320neo,A319,B737-800');
SELECT upsert_airport_co('BAQ','SKBQ','Barranquilla','Ernesto Cortissoz','Atlántico',10.889600,-74.780800,94,3000,'Costero','A320-200,A320neo,A319,B737-800');
SELECT upsert_airport_co('SMR','SKSM','Santa Marta','Simón Bolívar','Magdalena',11.119600,-74.230600,22,1900,'Costero','A320-200,A320neo,B737-800,ATR 72-600');
SELECT upsert_airport_co('CLO','SKCL','Cali','Alfonso Bonilla Aragón','Valle del Cauca',3.543000,-76.380200,3162,3000,'Valle','A320-200,A320neo,A321,B737-800');
SELECT upsert_airport_co('PEI','SKPE','Pereira','Matecaña','Risaralda',4.813200,-75.739500,4413,2080,'Ondulado','A320-200,A319,ATR 72-600');
SELECT upsert_airport_co('AXM','SKAR','Armenia','El Edén','Quindío',4.452800,-75.766400,3999,2050,'Ondulado','A320-200,A319,ATR 72-600');
SELECT upsert_airport_co('BGA','SKBG','Bucaramanga','Palonegro','Santander',7.126500,-73.184800,3897,3000,'Montañoso','A320-200,A319,B737-800');
SELECT upsert_airport_co('CUC','SKCC','Cúcuta','Camilo Daza','Norte de Santander',7.928600,-72.511500,1096,2300,'Valle','A320-200,A319,B737-800');
SELECT upsert_airport_co('ADZ','SKSP','San Andrés','Gustavo Rojas Pinilla','San Andrés',12.583600,-81.711200,19,2380,'Isla','A320-200,A320neo,B737-800');
SELECT upsert_airport_co('LET','SKLT','Leticia','Alfredo Vásquez Cobo','Amazonas',-4.193600,-69.943200,277,2500,'Selva','A320-200,A319,ATR 72-600');
SELECT upsert_airport_co('IBE','SKIB','Ibagué','Perales','Tolima',4.421600,-75.133300,2995,1800,'Montañoso','ATR 72-600,ERJ-145');
SELECT upsert_airport_co('RCH','SKRH','Riohacha','Almirante Padilla','La Guajira',11.526200,-72.926000,43,2100,'Costero','A320-200,ATR 72-600');
SELECT upsert_airport_co('NVA','SKNV','Neiva','Benito Salas','Huila',2.951400,-75.293600,1460,1880,'Valle','ATR 72-600,ERJ-145');
SELECT upsert_airport_co('MZL','SKMZ','Manizales','La Nubia','Caldas',5.029600,-75.464700,6890,1480,'Montañoso','ATR 42-600,ERJ-145');
SELECT upsert_airport_co('VUP','SKVP','Valledupar','Alfonso López Pumarejo','Cesar',10.435000,-73.249500,482,2100,'Valle','A320-200,ATR 72-600');
SELECT upsert_airport_co('UIB','SKUI','Quibdó','El Caraño','Chocó',5.690800,-76.641200,204,1800,'Selva','ATR 72-600,ERJ-145');
SELECT upsert_airport_co('PPN','SKPP','Popayán','Guillermo León Valencia','Cauca',2.454400,-76.609300,5689,1850,'Montañoso','ATR 72-600,ERJ-145');
SELECT upsert_airport_co('GPI','SKGP','Guapi','Juan Casiano Solís','Cauca',2.571200,-77.898600,164,1600,'Selva','ATR 42-600,ERJ-145');
SELECT upsert_airport_co('EYP','SKYP','Yopal','El Alcaraván','Casanare',5.319100,-72.384000,1020,2300,'Llano','A320-200,ATR 72-600');
SELECT upsert_airport_co('MTR','SKMR','Montería','Los Garzones','Córdoba',8.823700,-75.825800,36,2300,'Valle','A320-200,A320neo,B737-800');
SELECT upsert_airport_co('VVC','SKVV','Villavicencio','La Vanguardia','Meta',4.168000,-73.613800,1399,1800,'Llano','ATR 72-600,ERJ-145');
SELECT upsert_airport_co('CZU','SKCZ','Corozal (Sincelejo)','Las Brujas','Sucre',9.332750,-75.285600,512,1600,'Valle','ATR 72-600,ERJ-145');
SELECT upsert_airport_co('PSO','SKPS','Pasto','Antonio Nariño','Nariño',1.396200,-77.291500,5951,2300,'Montañoso','A320-200,ATR 72-600');
SELECT upsert_airport_co('RVE','SKSA','Saravena','Los Colonizadores','Arauca',6.955600,-71.857200,700,1800,'Llano','ATR 42-600,ERJ-145');
SELECT upsert_airport_co('APO','SKLC','Carepa (Apartadó)','Antonio Roldán Betancourt','Antioquia',7.810000,-76.716000,46,1800,'Selva','ATR 72-600,ERJ-145');