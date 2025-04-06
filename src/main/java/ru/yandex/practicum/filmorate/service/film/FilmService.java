package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.exception.ConstraintViolationException;
import ru.yandex.practicum.filmorate.exception.EmptyFieldException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.mappers.RatingMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    @Autowired // Внедрение зависимостей через конструктор
    public FilmService(FilmDbStorage filmDbStorage, UserDbStorage userDbStorage) {
        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
    }

    public void addLikeToFilm(long filmId, long userId) {
        filmIsExists(filmId);
        userIsExists(userId);

        if (filmDbStorage.getLikes(filmId).contains(userId)) {
            throw new RuntimeException("Этот пользователь уже поставил лайк!");
        } else {
            filmDbStorage.addLike(filmId, userId);
        }
    }

    public void deleteLikeToFilm(long filmId, long userId) {
        filmIsExists(filmId);
        userIsExists(userId);

        if (!filmDbStorage.getLikes(filmId).contains(userId)) {
            throw new RuntimeException("Этот пользователь не ставил лайк!");
        } else {
            filmDbStorage.removeLike(filmId, userId);
        }
    }

    public List<FilmDto> getTopFilms(int count) {
        return filmDbStorage.getFilms().stream()
                .sorted((film1, film2) -> {
                    int likes1 = filmDbStorage.getLikes(film1.getId()).size();
                    int likes2 = filmDbStorage.getLikes(film2.getId()).size();
                    return Integer.compare(likes2, likes1);
                })
                .limit(count)
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public Collection<FilmDto> getFilms() {
        return filmDbStorage.getFilms()
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto addFilm(Film film) {
        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.warn("Film release date is after 1895");
                throw new ConstraintViolationException("Film release date is after 1895");
            }
        }
        return FilmMapper.mapToFilmDto(filmDbStorage.addFilm(film));
    }

    public FilmDto updateFilm(Film film) {
        if (film.getId() == 0) {
            log.warn("Film id is empty");
            throw new EmptyFieldException("ID can't be empty");
        }

        return FilmMapper.mapToFilmDto(filmDbStorage.updateFilm(film));
    }

    public FilmDto getFilmById(@PathVariable long filmId) {
        return FilmMapper.mapToFilmDto(filmDbStorage.getFilmById(filmId));
    }

    private void userIsExists(long userId) {
        if (userDbStorage.getUserById(userId) == null) {
            throw new NotFoundException("пользователя с заданным id не существует");
        }
    }

    private void filmIsExists(long filmId) {
        if (filmDbStorage.getFilmById(filmId) == null) {
            throw new NotFoundException("пользователя с заданным id не существует");
        }
    }

    public Collection<GenreDto> getGenres() {
        return filmDbStorage.getGenres()
                .stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }

    public GenreDto getGenreById(int id) {
        return GenreMapper.mapToGenreDto(filmDbStorage.getGenreById(id));
    }

    public Collection<RatingDto> getRatings() {
        return filmDbStorage.getRating()
                .stream()
                .map(RatingMapper::mapToRatingDto)
                .collect(Collectors.toList());
    }

    public RatingDto getRatingById(int id) {
        return RatingMapper.mapToRatingDto(filmDbStorage.getRatingById(id));
    }
}
