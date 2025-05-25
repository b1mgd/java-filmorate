package filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import filmorate.dto.FilmDto;
import filmorate.mappers.FilmMapper;
import filmorate.model.Film;
import filmorate.service.film.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> getFilms() {
        log.info("Received GET /films request");
        return filmService.getFilms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto addFilm(@Valid @RequestBody FilmDto film) {
        log.info("Received POST /films request with body: {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody FilmDto filmDto) {
        log.info("Received PUT /films request with body: {}", filmDto);
        Film film = FilmMapper.mapToFilmDto(filmDto);
        return filmService.updateFilm(film);
    }

    @GetMapping("/{filmId}")
    public FilmDto getFilmById(@PathVariable long filmId) {
        log.info("Received GET /films/{} request", filmId);
        return filmService.getFilmById(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLikeToFilm(@PathVariable long filmId, @PathVariable long userId) {
        log.info("Received PUT /films/{}/like/{} request", filmId, userId);
        filmService.addLikeToFilm(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLikeToFilm(@PathVariable long filmId, @PathVariable long userId) {
        log.info("Received DELETE /films/{}/like/{} request", filmId, userId);
        filmService.deleteLikeToFilm(filmId, userId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getTopFilms(@RequestParam(defaultValue = "10") @Positive(message = "Count must be positive") int count,
                                     @RequestParam(defaultValue = "-1") int genreId,
                                     @RequestParam(defaultValue = "-1") int year) {
        log.info("Received GET /films/popular?count={}&genreId={}&year={} request", count, genreId, year);
        return filmService.getTopFilms(count, genreId, year);
    }

    @DeleteMapping("/{filmId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable long filmId) {
        log.info("Received DELETE /films/{} request", filmId);
        filmService.deleteFilm(filmId);
    }

    @GetMapping("/common")
    public List<FilmDto> getCommonFilms(
            @RequestParam long userId,
            @RequestParam long friendId) {
        log.info("GET common films for userId={} and friendId={}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDto> findByDirector(@PathVariable long directorId,
                                        @RequestParam(name = "sortBy") String sortMode) {
        log.info("Received GET /films/director/{}?sortBy={} request", directorId, sortMode);
        return filmService.findByDirector(directorId, sortMode);
    }

    @GetMapping("/search")
    public List<FilmDto> searchFilms(@RequestParam String query,
                                     @RequestParam String by) {
        log.info("GET /films/search?query={}&by={}", query, by);
        return filmService.searchFilms(query, by);
    }
}