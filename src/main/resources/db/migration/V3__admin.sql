INSERT INTO users (username, email, password)
VALUES ('admin', 'admin@gmail.com', 'admin');

WITH new_user AS (
    SELECT id FROM users WHERE username = 'admin'
)

INSERT INTO user_roles (user_id, role_id)
SELECT new_user.id, roles.id
FROM new_user, roles
WHERE roles.name = 'ROLE_ADMIN';
