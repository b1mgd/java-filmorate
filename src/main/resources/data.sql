INSERT INTO Users (email, login, name, birthday)
VALUES ('test@user.com', 'testlogin', 'Test User Name', '1991-11-11');

INSERT INTO Users (email, login, name, birthday)
VALUES ('test1@user.com', 'test2login', 'Another User', '1992-11-11');

INSERT INTO Rating(rating_id, rating_name)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');


INSERT INTO Genre (name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');