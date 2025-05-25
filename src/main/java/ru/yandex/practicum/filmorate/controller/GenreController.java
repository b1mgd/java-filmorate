package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    private FilmService filmService;

    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public ResponseEntity<List<GenreDto>> getGenres() {
        log.info("Получен запрос на получение всех жанров");
        return ResponseEntity.ok(filmService.getGenres());
    }

    @GetMapping("/{genreId}")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable Integer genreId) {
        log.info("Получен запрос на получение жанра с genreId: {}", genreId);
        return ResponseEntity.ok(filmService.getGenreById(genreId));
    }
}
