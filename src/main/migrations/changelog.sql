--liquibase formatted sql

--changeset magne:1
CREATE TABLE users (
  id       SERIAL PRIMARY KEY,
  username VARCHAR(255) UNIQUE INDEX,
  email    VARCHAR(255),
  passhash VARCHAR(255)
);

CREATE UNIQUE INDEX usernameindex ON users (username);

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

CREATE UNIQUE INDEX ratingindex ON ratings (user_id, rapper_id);