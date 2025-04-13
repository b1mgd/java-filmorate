package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    List<Film> getAllFilms();

    Optional<Film> getFilmById(Integer filmId);

    Film createFilm(Film newFilm);

    Film updateFilm(Film newFilm);

    boolean deleteFilm(Integer filmId);

    List<User> getLikes(Integer filmId);

    boolean addLike(Integer filmId, Integer userId);

    boolean removeLike(Integer filmId, Integer userId);

    Map<Integer, Set<Integer>> getAllFilmLikes();
}
