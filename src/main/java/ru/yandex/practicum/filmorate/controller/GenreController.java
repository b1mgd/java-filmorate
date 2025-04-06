package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final FilmService filmService;

    @GetMapping
    public Collection<GenreDto> getGenres() {
        log.info("Received GET /genres request");
        return filmService.getGenres();
    }

    @GetMapping("/{genreId}")
    public GenreDto getGenreById(@PathVariable int genreId) {
        log.info("Received GET /genres/{} request", genreId);
        return filmService.getGenreById(genreId);
    }
}