package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingController {
    private final FilmService filmService;

    @GetMapping
    public Collection<RatingDto> getRatings() {
        log.info("Received GET /mpa request");
        return filmService.getRatings();
    }

    @GetMapping("/{ratingId}")
    public RatingDto getRatingById(@PathVariable int ratingId) {
        log.info("Received GET /mpa/{} request", ratingId);
        return filmService.getRatingById(ratingId);
    }
}