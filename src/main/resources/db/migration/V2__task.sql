CREATE TABLE task (
                       id SERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       description VARCHAR(255) NOT NULL,
                       status VARCHAR(20) NOT NULL,
                       priority VARCHAR(20) NOT NULL,
                       creator_id INT NOT NULL,
                       FOREIGN KEY (creator_id) REFERENCES users (id)
);

CREATE TABLE task_executors (
                                task_id INT NOT NULL,
                                user_id INT NOT NULL,
                                FOREIGN KEY (task_id) REFERENCES task (id),
                                FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE comment (
                          id SERIAL PRIMARY KEY,
                          text VARCHAR(255) NOT NULL,
                          user_id INT NOT NULL,
                          task_id INT NOT NULL,
                          FOREIGN KEY (user_id) REFERENCES users (id),
                          FOREIGN KEY (task_id) REFERENCES task (id)
);
