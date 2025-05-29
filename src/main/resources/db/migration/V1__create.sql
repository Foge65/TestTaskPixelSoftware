CREATE TABLE "user"
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(500),
    date_of_birth DATE,
    password      VARCHAR(500) CHECK (LENGTH(password) >= 8)
);

CREATE TABLE account
(
    id      BIGSERIAL PRIMARY KEY,
    user_id SERIAL UNIQUE,
    balance DECIMAL,
    FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE TABLE email_data
(
    id      BIGSERIAL PRIMARY KEY,
    user_id SERIAL,
    email   VARCHAR(200) UNIQUE,
    FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE TABLE phone_data
(
    id      BIGSERIAL PRIMARY KEY,
    user_id SERIAL,
    phone   VARCHAR(13) UNIQUE,
    FOREIGN KEY (user_id) REFERENCES "user" (id)
);
