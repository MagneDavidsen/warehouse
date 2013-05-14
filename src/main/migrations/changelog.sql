--liquibase formatted sql

--changeset magne:1
create table users (
    id int primary key,
    name varchar(255)
);