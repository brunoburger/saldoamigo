CREATE TABLE IF NOT EXISTS users (
    id serial NOT NULL,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    role varchar(10) default 'user' check (role in ('admin', 'user')),
    CONSTRAINT pk_users PRIMARY KEY (id)
);
