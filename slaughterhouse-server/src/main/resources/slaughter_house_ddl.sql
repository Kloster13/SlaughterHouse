DROP SCHEMA IF EXISTS slaughter_house CASCADE;
CREATE SCHEMA IF NOT EXISTS slaughter_house;
set search_path = "slaughter_house";

CREATE SEQUENCE IF NOT EXISTS farm_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS animal_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS tray_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS animal_part_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS farm
(
    id   BIGINT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS animal
(
    id           BIGINT PRIMARY KEY,
    farm_id      BIGINT REFERENCES farm (id),
    arrival_date Date,
    type         VARCHAR(255),
    weight       DOUBLE PRECISION
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
    id            BIGINT PRIMARY KEY DEFAULT nextval('product_id_seq'),
    serial        varchar(255),
    packaged_Date DATE,
    weight        DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS half_animal
(
    id            BIGINT PRIMARY KEY DEFAULT nextval('product_id_seq'),
    serial        varchar(255),
    packaged_Date DATE,
    weight        DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS product_tray
(
    product_id BIGINT NOT NULL,
    tray_id    BIGINT NOT NULL REFERENCES tray (id),
    PRIMARY KEY (product_id, tray_id)
);

INSERT INTO farm (id, name)
VALUES (1, 'Nørgaard Gård'),
       (2, 'Solskin Farm'),
       (3, 'Grøn Mark'),
       (4, 'Vestjysk Landbrug'),
       (5, 'Bakkegården');

INSERT INTO animal (id, farm_id, arrival_date, type, weight)
VALUES (1, 1, '2026-01-10', 'COW', 620.5),
       (2, 2, '2026-01-10', 'PIG', 110.0),
       (3, 3, '2026-01-10', 'CHICKEN', 2.4),
       (4, 4, '2026-01-10', 'COW', 580.0),
       (5, 5, '2026-01-10', 'PIG', 95.3);

INSERT INTO tray (id, max_weight, weight)
VALUES (1, 200, 150.0),
       (2, 150, 80.0),
       (3, 100, 45.5),
       (4, 250, 200.0),
       (5, 180, 120.0);

INSERT INTO animal_part (id, animal_id, tray_id, weight, part_type)
VALUES (1, 1, 1, 150.0, 'leg'),
       (2, 2, 2, 20.0, 'shoulder'),
       (3, 3, 3, 0.5, 'breast'),
       (4, 4, 4, 180.0, 'ribs'),
       (5, 5, 5, 15.0, 'loin');

-- package og half_animal deler product_id_seq (TABLE_PER_CLASS kræver unikke id'er på tværs af hele Product-hierarkiet)
INSERT INTO package (id, serial, packaged_date, weight)
VALUES (nextval('product_id_seq'), 'PKG-0001', '2026-01-10', 12.5),
       (nextval('product_id_seq'), 'PKG-0002', '2026-01-11', 8.3),
       (nextval('product_id_seq'), 'PKG-0003', '2026-01-12', 15.0),
       (nextval('product_id_seq'), 'PKG-0004', '2026-01-13', 9.7),
       (nextval('product_id_seq'), 'PKG-0005', '2026-01-14', 11.2);

INSERT INTO half_animal (id, serial, packaged_date, weight)
VALUES (nextval('product_id_seq'), 'HALF-0001', '2026-01-10', 210.0),
       (nextval('product_id_seq'), 'HALF-0002', '2026-01-11', 195.5),
       (nextval('product_id_seq'), 'HALF-0003', '2026-01-12', 230.0),
       (nextval('product_id_seq'), 'HALF-0004', '2026-01-13', 205.0),
       (nextval('product_id_seq'), 'HALF-0005', '2026-01-14', 220.8);

-- Ekstra trays som flere dyr bidrager til, for at kunne vise tracing mellem dyr og products
INSERT INTO tray (id, max_weight, weight)
VALUES (6, 220, 160.0),
       (7, 130, 70.0),
       (8, 90, 30.0);

-- Dyr 1 og 4 bidrager begge til tray 6 (co-mingling), dyr 2 bidrager to forskellige dele til to trays
INSERT INTO animal_part (id, animal_id, tray_id, weight, part_type)
VALUES (6, 1, 6, 90.0, 'shoulder'),
       (7, 4, 6, 70.0, 'flank'),
       (8, 2, 7, 55.0, 'belly'),
       (9, 3, 8, 0.3, 'wing');

INSERT INTO product_tray (product_id, tray_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (6, 4),
       (7, 5),
       -- package 1 er også bygget af tray 6 -> kan spores til dyr 1 OG dyr 4
       (1, 6),
       -- package 2 trækker på to trays fra samme dyr (dyr 2)
       (2, 7),
       -- half_animal id 8 kan spores til dyr 3 via tray 8
       (8, 8),
       -- tray 1 (dyr 1's "leg") indgår også i half_animal id 6 -> samme tray i to forskellige products
       (6, 1);

-- Saet sekvenserne efter de id er der er indsat ovenfor (ogsaa hvis seed-data kommenteres ind)
SELECT setval('farm_seq', COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM farm;
SELECT setval('animal_seq', COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM animal;
SELECT setval('tray_seq', COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM tray;
SELECT setval('animal_part_seq', COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM animal_part;
