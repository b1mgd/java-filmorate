package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
@Qualifier("filmRepository")
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT f.*, m.rating FROM film f " +
            "JOIN mpa m ON f.mpa_id = m.mpa_id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.*, m.rating FROM film f " +
            "JOIN mpa m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO film " +
            "(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE film SET " +
            "name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String GET_LIKES_QUERY = "SELECT * FROM users u " +
            "JOIN film_likes f ON u.user_id = f.user_id WHERE f.film_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM film WHERE film_id = ?";
    private static final String ADD_LIKE_QUERY = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    private static final String DELETE_GENRES_BY_FILM_ID_QUERY = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String ADD_GENRE_TO_FILM_QUERY = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> rowMapper) {
        super(jdbc, rowMapper);
    }

    @Override
    public List<Film> getAllFilms() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Film> getFilmById(Integer filmId) {
        Optional<Film> filmOptional = findOne(FIND_BY_ID_QUERY, filmId);

        if (filmOptional.isPresent()) {
            Set<Genre> genres = getGenresByFilmId(filmId);
            Film film = filmOptional.get();
            film.setGenres(genres);
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Film createFilm(Film newFilm) {
        int id = insert(
                INSERT_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpa() != null ? newFilm.getMpa().getId() : null
        );

        newFilm.setId(id);
        log.info("Новый фильм сохранен {}", newFilm);

        return newFilm;
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        update(
                UPDATE_QUERY,
                updatedFilm.getName(),
                updatedFilm.getDescription(),
                updatedFilm.getReleaseDate(),
                updatedFilm.getDuration(),
                updatedFilm.getMpa() != null ? updatedFilm.getMpa().getId() : null,
                updatedFilm.getId()
        );
        log.info("Данные по фильму обновлены: {}", updatedFilm);

        return updatedFilm;
    }

    @Override
    public boolean deleteFilm(Integer filmId) {
        if (delete(DELETE_QUERY, filmId)) {
            log.info("Фильм с filmId {} был удален", filmId);
            return true;
        } else {
            log.warn("Не удалось удалить фильм с filmId {}", filmId);
            return false;
        }
    }

    @Override
    public List<User> getLikes(Integer filmId) {
        String query = GET_LIKES_QUERY;
        List<User> queryResult = jdbc.query(query, new UserRowMapper(), filmId);

        if (queryResult.isEmpty()) {
            log.warn("Записей по запросу {} не было найдено", query);
        } else {
            log.info("Найдены записи по запросу {}", query);
        }

        return queryResult;
    }

    @Override
    public boolean addLike(Integer filmId, Integer userId) {
        int rowsAdded = jdbc.update(ADD_LIKE_QUERY, filmId, userId);
        boolean addLikeResult = rowsAdded != 0;

        if (!addLikeResult) {
            log.warn("Лайк пользователя с userId {} не был добавлен фильму с filmId {}", userId, filmId);
        } else {
            log.info("Поставлен лайк пользователя с userId {} фильму с filmId {}", userId, filmId);
        }

        return addLikeResult;
    }

    @Override
    public boolean removeLike(Integer filmId, Integer userId) {
        int rowsDeleted = jdbc.update(REMOVE_LIKE_QUERY, filmId, userId);
        boolean removeLikeResult = rowsDeleted != 0;

        if (!removeLikeResult) {
            log.warn("Не удалось удалить лайк пользователя с userId {} фильму с filmId {}", userId, filmId);
        } else {
            log.info("Лайк пользователя с userId {} удален с фильма с filmId {}", userId, filmId);
        }

        return removeLikeResult;
    }

    public void deleteGenresByFilmId(Integer filmId) {
        jdbc.update(DELETE_GENRES_BY_FILM_ID_QUERY, filmId);
        log.info("Удалены жанры для фильма с filmId {}", filmId);
    }

    public void addGenreToFilm(Integer filmId, Integer genreId) {
        jdbc.update(ADD_GENRE_TO_FILM_QUERY, filmId, genreId);
        log.info("Жанр с id {} добавлен для фильма с id {}", genreId, filmId);
    }

    private Set<Genre> getGenresByFilmId(int filmId) {
        String sql = "SELECT g.genre_id, g.name FROM genre g " +
                "JOIN film_genre fg ON fg.genre_id = g.genre_id WHERE fg.film_id = ?";
        return new LinkedHashSet<>(jdbc.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId));
    }
}
