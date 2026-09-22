DROP SCHEMA IF EXISTS slaughter_house CASCADE;
CREATE SCHEMA IF NOT EXISTS slaughter_house;
set search_path = "slaughter_house";

CREATE TABLE IF NOT EXISTS farm
(
    id   BIGINT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS animal
(
    id      BIGINT PRIMARY KEY,
    farm_id BIGINT REFERENCES farm (id),
    type    VARCHAR(255),
    weight  DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS tray
(
    id         BIGINT PRIMARY KEY,
    max_weight INTEGER,
    weight     DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS animal_part
(
    id        BIGINT PRIMARY KEY,
    animal_id BIGINT REFERENCES animal (id),
    tray_id   BIGINT REFERENCES tray (id),
    weight    DOUBLE PRECISION,
    part_type VARCHAR(255)
);
CREATE SEQUENCE IF NOT EXISTS product_id_seq;

CREATE TABLE IF NOT EXISTS package
(
    id           BIGINT PRIMARY KEY DEFAULT nextval('product_id_seq'),
    serial       varchar(255),
    packaged_Date DATE,
    weight       DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS half_animal
(
    id           BIGINT PRIMARY KEY DEFAULT nextval('product_id_seq'),
    serial       varchar(255),
    packaged_Date DATE,
    weight       DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS product_tray
(
    product_id BIGINT NOT NULL,
    tray_id    BIGINT NOT NULL REFERENCES tray (id),
    PRIMARY KEY (product_id, tray_id)
);