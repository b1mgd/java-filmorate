package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final FilmService filmService;

    @Autowired
    public GenreController(FilmDbStorage filmDbStorage, UserDbStorage userDbStorage) {
        filmService = new FilmService(filmDbStorage, userDbStorage);
    }

    @GetMapping
    public Collection<GenreDto> getGenres() {
        log.info("getGenres");
        return filmService.getGenres();
    }

    @GetMapping("/{genreId}")
    public GenreDto getGenreById(@PathVariable int genreId) {
        log.info("getGenreById");
        return filmService.getGenreById(genreId);
    }
}
