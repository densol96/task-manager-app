INSERT INTO users(email, first_name, last_name, `role`) 
VALUES ('solo@deni.com', 'Deniss', 'Solovjovs', 'USER');

UPDATE project_members
SET user_id = 1
WHERE id = 1;