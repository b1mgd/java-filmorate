package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public ResponseEntity<Collection<Film>> getAllFilms() {
        log.info("Получен запрос на вывод списка фильмов: {}", films.values());
        return ResponseEntity.status(HttpStatus.OK).body(films.values());
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film newFilm) {
        log.info("Получен запрос на добавление фильма: {}", newFilm);

        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)) ||
                newFilm.getDescription().length() > 200) {
            log.warn("Были представлены некорректные данные {} или {}, поэтому фильм не был добавлен",
                    newFilm.getReleaseDate(), newFilm.getDescription());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(newFilm);
        }

        Integer id = getNextId();
        newFilm.setId(id);
        films.put(id, newFilm);
        log.info("Фильм {} был добавлен в список", newFilm);

        return ResponseEntity.status(HttpStatus.CREATED).body(newFilm);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film newFilm) {
        log.info("Получен запрос на обновление фильма: {}", newFilm);

        if (newFilm.getId() == null || !films.containsKey(newFilm.getId())) {
            log.warn("Фильм с указанным ID не был найден");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(newFilm);
        }

        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)) ||
                newFilm.getDescription().length() > 200) {
            log.warn("Были представлены некорректные данные {} или {}, поэтому фильм не был добавлен",
                    newFilm.getReleaseDate(), newFilm.getDescription());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(newFilm);
        }

        Film film = films.get(newFilm.getId());
        film.setName(newFilm.getName());
        film.setDescription(newFilm.getDescription());
        film.setReleaseDate(newFilm.getReleaseDate());
        film.setDuration(newFilm.getDuration());
        log.info("Информация о фильме была обновлена: {}", film);

        return ResponseEntity.status(HttpStatus.OK).body(film);
    }

    private Integer getNextId() {
        int currentId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentId;
    }
}
