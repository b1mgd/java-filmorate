package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public ResponseEntity<List<FilmDto>> getAllFilms() {
        log.info("Получен запрос на вывод фильмов");
        return ResponseEntity.ok(filmService.getAllFilms());
    }

    @GetMapping("/{filmId}")
    public ResponseEntity<FilmDto> getFilmById(@PathVariable Integer filmId) {
        log.info("Получен запрос на фильм с filmId {}", filmId);
        return ResponseEntity.ok(filmService.getFilmById(filmId));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<FilmDto>> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос на вывод {} наиболее популярных фильмов", count);
        return ResponseEntity.ok(filmService.getPopularFilms(count));
    }

    @PostMapping
    public ResponseEntity<FilmDto> addFilm(@Valid @RequestBody NewFilmRequest request) {
        log.info("Получен запрос на добавление фильма: {}", request);
        return ResponseEntity.status(HttpStatus.CREATED).body(filmService.createFilm(request));
    }

    @PutMapping
    public ResponseEntity<FilmDto> updateFilm(@Valid @RequestBody UpdateFilmRequest request) {
        log.info("Обновление фильма: {}", request);
        return ResponseEntity.ok(filmService.updateFilm(request));
    }

    @PutMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable Integer filmId,
                                        @PathVariable Integer userId) {
        log.info("Получен запрос на добавление пользователем {} лайка фильму с filmId {}", userId, filmId);
        filmService.addLike(filmId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable Integer filmId,
                                           @PathVariable Integer userId) {
        log.info("Пользователь {} отправил запрос на удаление лайка с фильма {}", userId, filmId);
        filmService.deleteLike(filmId, userId);
        return ResponseEntity.ok().build();
    }
}
