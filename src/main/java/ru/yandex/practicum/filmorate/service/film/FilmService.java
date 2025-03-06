package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final InMemoryUserStorage inMemoryUserStorage;
    private final InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired // Внедрение зависимостей через конструктор
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public void addLikeToFilm(int filmId, int userId) {
        filmIsExists(filmId);
        userIsExists(userId);

        if (inMemoryFilmStorage.getFilmById(filmId).getWhoLiked().contains(userId)) {
            throw new RuntimeException("Этот пользователь уже поставил лайк!");
        } else {
            inMemoryFilmStorage.getFilmById(filmId).getWhoLiked().add(userId);
        }
    }

    public void deleteLikeToFilm(int filmId, int userId) {
        filmIsExists(filmId);
        userIsExists(userId);

        if (!inMemoryFilmStorage.getFilmById(filmId).getWhoLiked().contains(userId)) {
            throw new RuntimeException("Этот пользователь не ставил лайк!");
        } else {
            inMemoryFilmStorage.getFilmById(filmId).getWhoLiked().remove(userId);
        }
    }

    public List<Film> getTopFilms(int count) {
        return inMemoryFilmStorage.getFilms().stream()
                .sorted((film1, film2) ->
                        Integer.compare(film2.getWhoLiked().size(), film1.getWhoLiked().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void userIsExists(int userId) {
        if (inMemoryUserStorage.getUserById(userId) == null) {
            throw new NotFoundException("пользователя с заданным id не существует");
        }
    }

    private void filmIsExists(int filmId) {
        if (inMemoryFilmStorage.getFilmById(filmId) == null) {
            throw new NotFoundException("пользователя с заданным id не существует");
        }
    }
}
