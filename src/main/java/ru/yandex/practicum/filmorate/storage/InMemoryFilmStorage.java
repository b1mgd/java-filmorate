package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public void addFilm(Film newFilm) {
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм {} был добавлен в хранилище", newFilm);
    }

    @Override
    public void updateFilm(Film newFilm) {
        if (!films.containsKey(newFilm.getId())) {
            log.warn("Попытка обновления несуществующего фильма с id {}", newFilm.getId());
            throw new NotFoundException("Фильм с id " + newFilm.getId() + " не найден");
        }
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм {} был обновлен в хранилище", newFilm);
    }

    @Override
    public Set<Integer> getFilmIds() {
        log.info("Был выдан список ключей {} из хранилища", films.keySet());
        return Set.copyOf(films.keySet());
    }

    @Override
    public Collection<Film> getFilms() {
        log.info("Был выдан список значений {} из хранилища", films.values());
        return List.copyOf(films.values());
    }

    @Override
    public Optional<Film> getFilmById(Integer id) {
        log.info("Был выдан фильм {} из хранилища", films.get(id));
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void likeFilm(Integer id, Integer userId) {
        Film film = films.get(id);
        if (film == null) {
            log.warn("Попытка лайкнуть несуществующий фильм с id {}", id);
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        film.getLikes().add(userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, id);
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        Film film = films.get(id);
        if (film == null) {
            log.warn("Попытка удалить лайк у несуществующего фильма с id {}", id);
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        film.getLikes().remove(userId);
        log.info("Пользователь {} удалил лайк с фильма {}", userId, id);
    }
}
