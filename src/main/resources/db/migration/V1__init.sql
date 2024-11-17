CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(20) NOT NULL
);

CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE user_roles (
                            user_id INT NOT NULL,
                            role_id INT NOT NULL,
                            FOREIGN KEY (user_id) REFERENCES users (id),
                            FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE refresh_token (
                               id SERIAL PRIMARY KEY,
                               user_id INT NOT NULL,
                               token VARCHAR(255) NOT NULL UNIQUE,
                               expiry_date TIMESTAMP NOT NULL,
                               FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');