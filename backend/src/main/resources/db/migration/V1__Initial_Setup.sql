CREATE TABLE customer(
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL unique,
    password TEXT NOT NULL,
    age int NOT NULL,
    gender TEXT NULL
);
