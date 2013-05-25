--liquibase formatted sql

--changeset magne:1
create table users (
    id serial primary key,
    name varchar(255)
);

create table rappers (
  id serial primary key,
  name varchar(255)
);

create table ratings (
  id serial primary key,
  user_id int references users (id),
  rapper_id int references rappers (id),
  rating int
);