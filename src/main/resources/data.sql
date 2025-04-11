MERGE INTO genre (name) KEY (name)
    VALUES ('Комедия'),
           ('Драма'),
           ('Мультфильм'),
           ('Триллер'),
           ('Документальный'),
           ('Боевик');

MERGE INTO mpa (rating) KEY (rating)
    VALUES ('G'),
           ('PG'),
           ('PG-13'),
           ('R'),
           ('NC-17');