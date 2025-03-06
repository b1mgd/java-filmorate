package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface FilmStorage {
    Map<Integer, Film> films = new HashMap<>();

    Collection<Film> getFilms();

    Film addFilm(@RequestBody @Valid Film film);

    Film updateFilm(@RequestBody Film film);

    int setFilmId();

    Film getFilmById(int filmId);
}
