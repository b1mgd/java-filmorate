package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public ResponseEntity<Collection<Film>> getAllFilms() {
        log.info("Получен запрос на вывод фильмов");
        return ResponseEntity.ok(filmService.getAllFilms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable Integer id) {
        log.info("Получен запрос на фильм с id {}", id);
        return ResponseEntity.ok(filmService.getFilmById(id));
    }

    @GetMapping("/popular")
    public ResponseEntity<Collection<Film>> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос на вывод {} наиболее популярных фильмов", count);
        return ResponseEntity.ok(filmService.getPopularFilms(count));
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film newFilm) {
        log.info("Получен запрос на добавление фильма: {}", newFilm);
        return ResponseEntity.status(HttpStatus.CREATED).body(filmService.addFilm(newFilm));
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film newFilm) {
        log.info("Обновление фильма {} с id {}", newFilm, newFilm.getId());
        return ResponseEntity.ok(filmService.updateFilm(newFilm));
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> likeFilm(@PathVariable Integer id,
                                         @PathVariable Integer userId) {
        log.info("Получен запрос на добавление пользователем {} лайка фильму с id {}", userId, id);
        filmService.likeFilm(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> deleteLike(@PathVariable Integer id,
                                           @PathVariable Integer userId) {
        log.info("Пользователь {} отправил запрос на удаление лайка с фильма {}", userId, id);
        filmService.deleteLike(id, userId);
        return ResponseEntity.ok().build();
    }
}
