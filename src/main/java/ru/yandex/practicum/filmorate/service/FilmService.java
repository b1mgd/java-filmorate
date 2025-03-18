package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class FilmService {

    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с указанным Id не был найден"));
    }

    public Collection<Film> getPopularFilms(int count) {
        Collection<Film> films = Optional.ofNullable(filmStorage.getFilms()).orElse(Collections.emptyList());

        return films.stream()
                .sorted(Comparator.comparingInt(film -> -(film.getLikes() == null ? 0 : film.getLikes().size())))
                .limit(Math.min(count, films.size()))
                .toList();
    }

    public Film addFilm(Film newFilm) {
        if (isIncorrectFilm(newFilm)) {
            log.warn("Были представлены некорректные данные {} или {}, поэтому фильм не был добавлен",
                    newFilm.getReleaseDate(), newFilm.getDescription());
            throw new ValidateException("Были переданы некорректные сведения о дате выхода фильма " +
                    newFilm.getReleaseDate() + " или его описании" + newFilm.getDescription());
        }

        newFilm.setId(getNextId());
        filmStorage.addFilm(newFilm);

        return newFilm;
    }

    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null || !filmStorage.getFilmIds().contains(newFilm.getId())) {
            log.warn("Фильм с Id {} не был найден", newFilm.getId());
            throw new NotFoundException("Фильм с Id " + newFilm.getId() + "не был найден");
        }

        if (isIncorrectFilm(newFilm)) {
            log.warn("Были представлены некорректные данные {} или {}, поэтому фильм не был добавлен",
                    newFilm.getReleaseDate(), newFilm.getDescription());
            throw new ValidateException("Были переданы некорректные сведения о дате выхода фильма " +
                    newFilm.getReleaseDate() + " или его описании" + newFilm.getDescription());
        }

        filmStorage.updateFilm(newFilm);

        return newFilm;
    }

    public void likeFilm(Integer id, Integer userId) {
        validateFilmAndUser(id, userId);
        filmStorage.likeFilm(id, userId);
        log.info("Лайк пользователя {} добавлен фильму {}", userId, id);
    }

    public void deleteLike(Integer id, Integer userId) {
        validateFilmAndUser(id, userId);
        filmStorage.deleteLike(id, userId);
        log.info("Лайк пользователя {} удален с фильма {}", userId, id);
    }

    private void validateFilmAndUser(Integer id, Integer userId) {
        if (filmStorage.getFilmById(id).isEmpty()) {
            log.warn("Фильм с Id {} не был найден", id);
            throw new NotFoundException("Фильм с Id " + id + " не был найден");
        }
        if (userStorage.getUserById(userId).isEmpty()) {
            log.warn("Пользователь с Id {} не был найден", userId);
            throw new NotFoundException("Пользователь с " + userId + " не был найден");
        }
    }

    private Integer getNextId() {
        int currentId = filmStorage.getFilmIds()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentId;
    }

    private boolean isIncorrectFilm(Film newFilm) {
        return newFilm.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE) ||
                newFilm.getDescription().length() > MAX_DESCRIPTION_LENGTH;
    }
}
