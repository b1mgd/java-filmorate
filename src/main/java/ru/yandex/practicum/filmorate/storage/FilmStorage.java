package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    Collection<Film> getFilms();

    Optional<Film> getFilmById(Integer id);

    Set<Integer> getFilmIds();

    void addFilm(Film newFilm);

    void updateFilm(Film newFilm);

    void likeFilm(Integer id, Integer userId);

    void deleteLike(Integer id, Integer userId);
}
