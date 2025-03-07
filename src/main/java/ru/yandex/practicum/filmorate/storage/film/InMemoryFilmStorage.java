package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryFilmStorage implements FilmStorage {
    Map<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(setFilmId());
        log.debug("Film id set to {}", film.getId());
        films.put(film.getId(), film);
        log.info("Film added");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());
            if (film.getName() != null && !film.getName().isBlank()) {
                log.debug("Updating film with name {}", film.getName());
                oldFilm.setName(film.getName());
            }
            if (film.getDescription() != null && !film.getDescription().isBlank()) {
                log.debug("Updating film with description {}", film.getDescription());
                oldFilm.setDescription(film.getDescription());
            }
            if (film.getReleaseDate() != null) {
                log.debug("Updating film with releaseDate {}", film.getReleaseDate());
                oldFilm.setReleaseDate(film.getReleaseDate());
            }
            if (film.getDuration() != 0) {
                log.debug("Updating film with duration {}", film.getDuration());
                oldFilm.setDuration(film.getDuration());
            }
            log.info("Film updated");
            return oldFilm;
        }
        log.warn("Film not found");
        throw new NotFoundException("Film not found");
    }

    @Override
    public int setFilmId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Film getFilmById(int filmId) {
        if (films.get(filmId) == null) {
            throw new NotFoundException("Film not found");
        }
        return films.get(filmId);
    }


}
