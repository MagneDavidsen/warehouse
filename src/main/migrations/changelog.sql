--liquibase formatted sql

--changeset magne:1
create table users (
    id int primary key,
    name varchar(255)
);

--changeset magne:2
create table rappers (
  id int primary key,
  name varchar(255)
);

create table ratings (
  id int primary key,
  user_id int references users (id),
  rapper_id int references rappers (id),
  rating int
);