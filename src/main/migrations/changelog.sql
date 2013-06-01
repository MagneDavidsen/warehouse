--liquibase formatted sql

--changeset magne:1
CREATE TABLE users (
  id       SERIAL PRIMARY KEY,
  username VARCHAR(255),
  email    VARCHAR(255),
  passhash VARCHAR(255)
);

CREATE TABLE rappers (
  id   SERIAL PRIMARY KEY,
  name VARCHAR(255)
);

CREATE TABLE ratings (
  id        SERIAL PRIMARY KEY,
  user_id   INT REFERENCES users (id),
  rapper_id INT REFERENCES rappers (id),
  rating    INT
);