package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmDbStorage filmDbStorage, UserDbStorage userDbStorage) {
        filmService = new FilmService(filmDbStorage, userDbStorage);
    }


    @GetMapping
    public Collection<FilmDto> getFilms() {
        log.info("getFilms");
        return filmService.getFilms();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public FilmDto addFilm(@RequestBody @Valid Film film) {
        log.info("addFilm");
        return filmService.addFilm(film);
    }

    @PutMapping
    public FilmDto updateFilm(@RequestBody Film film) {
        log.info("updateFilm");
        return filmService.updateFilm(film);
    }

    @GetMapping("/{filmId}")
    public FilmDto getFilmById(@PathVariable int filmId) {
        log.info("getFilmById");
        return filmService.getFilmById(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLikeToFilm(@PathVariable int filmId, @PathVariable int userId) {
        log.info("addLikeToFilm");
        filmService.addLikeToFilm(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLikeToFilm(@PathVariable int filmId, @PathVariable int userId) {
        log.info("deleteLikeToFilm");
        filmService.deleteLikeToFilm(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("getTopFilms");
        return filmService.getTopFilms(count);
    }
}