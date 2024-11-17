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

CREATE TABLE task (
                      id SERIAL PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      description VARCHAR(255) NOT NULL,
                      status VARCHAR(50) NOT NULL,
                      priority VARCHAR(50) NOT NULL,
                      creator_id INT NOT NULL,
                      FOREIGN KEY (creator_id) REFERENCES users (id)
);

CREATE TABLE comment (
                         id SERIAL PRIMARY KEY,
                         text VARCHAR(255) NOT NULL,
                         user_id INT NOT NULL,
                         task_id INT NOT NULL,
                         FOREIGN KEY (user_id) REFERENCES users (id),
                         FOREIGN KEY (task_id) REFERENCES task (id)
);

CREATE TABLE task_executors (
                                task_id INT NOT NULL,
                                user_id INT NOT NULL,
                                FOREIGN KEY (task_id) REFERENCES task (id),
                                FOREIGN KEY (user_id) REFERENCES users (id)
);

INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');
