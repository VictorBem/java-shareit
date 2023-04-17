DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(200),
    email varchar(100));

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(200),
    description varchar(1000),
    available boolean,
    owner_id BIGINT,
    request_id BIGINT,
    CONSTRAINT items_to_users_fk
    FOREIGN KEY(owner_id) REFERENCES users(id) ON DELETE RESTRICT);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description varchar(1000),
    requestor_id BIGINT,
    created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT items_to_users_fk_1
    FOREIGN KEY(requestor_id) REFERENCES users(id) ON DELETE RESTRICT);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    item_id BIGINT,
    booker_id BIGINT,
    status varchar(20),
    CONSTRAINT requests_to_user_fk_2
    FOREIGN KEY(booker_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT requests_to_item_fk_2
    FOREIGN KEY(item_id) REFERENCES items(id) ON DELETE RESTRICT);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text varchar(1000),
    end_date TIMESTAMP WITHOUT TIME ZONE,
    item_id BIGINT,
    author_id BIGINT,
    created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT comments_to_user_fk_3
    FOREIGN KEY(author_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT requests_to_item_fk_3
    FOREIGN KEY(item_id) REFERENCES items(id) ON DELETE RESTRICT);