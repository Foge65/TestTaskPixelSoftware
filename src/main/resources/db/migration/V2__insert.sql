INSERT INTO "user" (name, date_of_birth, password)
VALUES ('Иван Иванов', '1990-05-01', 'password123'),
       ('Мария Смирнова', '1985-10-15', 'securePass456'),
       ('Алексей Кузнецов', '1993-02-20', 'myPass890');

INSERT INTO account (user_id, balance)
VALUES (1, 10000.50),
       (2, 2500.75),
       (3, 78999.99);

INSERT INTO email_data (user_id, email)
VALUES (1, 'ivan.ivanov@example.com'),
       (2, 'maria.smirnova@example.com'),
       (3, 'alex.kuznetsov@example.com');

INSERT INTO phone_data (user_id, phone)
VALUES (1, '79201234567'),
       (2, '79207654321'),
       (3, '79209876543');
