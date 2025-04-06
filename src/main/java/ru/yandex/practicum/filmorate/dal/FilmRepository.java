package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mapper.RatingRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    private static final String FIND_ALL_QUERY = "SELECT f.*, r.rating_name FROM films AS f JOIN rating AS r ON f.rating_id = r.rating_id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.*, r.rating_name FROM films AS f JOIN rating AS r ON f.rating_id = r.rating_id WHERE f.id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";
    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";
    private static final String GET_LIKES_QUERY = "SELECT user_id FROM Likes WHERE film_id = ?";
    private static final String ADD_LIKE_QUERY = "INSERT INTO Likes (user_id, film_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM Likes WHERE film_id = ? AND user_id = ?";

    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmMapper;
    private final GenreRepository genreRepository;

    public FilmRepository(JdbcTemplate jdbc, FilmRowMapper filmMapper, GenreRepository genreRepository) {
        super(jdbc, filmMapper);
        this.jdbc = jdbc;
        this.filmMapper = filmMapper;
        this.genreRepository = genreRepository;
    }

    public List<Film> findAll() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        films.forEach(this::loadGenres);
        films.forEach(this::loadRating);
        return films;
    }

    public Optional<Film> findById(long id) {
        Optional<Film> filmOpt = findOne(FIND_BY_ID_QUERY, id);
        filmOpt.ifPresent(film -> {
            loadGenres(film);
            loadRating(film);
        });
        return filmOpt;
    }

    public Film save(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null);
            ps.setInt(4, film.getDuration());
            if (film.getMpa() == null || film.getMpa().getId() == 0) {
                throw new IllegalArgumentException("Film Rating (MPA) ID cannot be null or zero");
            }
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(id);

        saveGenres(film);
        loadRating(film);

        return film;
    }

    public Film update(Film film) {
        int updatedRows = jdbc.update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null,
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        if (updatedRows == 0) {
            return null;
        }

        deleteGenres(film.getId());
        saveGenres(film);
        loadRating(film);

        return film;
    }


    private void saveGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        List<Object[]> batchArgs = film.getGenres().stream()
                .filter(Objects::nonNull)
                .map(genre -> new Object[]{film.getId(), genre.getId()})
                .collect(Collectors.toList());
        if (!batchArgs.isEmpty()) {
            jdbc.batchUpdate(INSERT_FILM_GENRE_QUERY, batchArgs);
        }
    }

    private void deleteGenres(long filmId) {
        jdbc.update(DELETE_FILM_GENRES_QUERY, filmId);
    }

    private void loadGenres(Film film) {
        Set<Genre> genres = genreRepository.findGenresByFilmId(film.getId());
        film.setGenres(genres);
    }

    private void loadRating(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() != 0) {
            String sql = "SELECT rating_id, rating_name FROM rating WHERE rating_id = ?";
            try {
                Rating fullRating = jdbc.queryForObject(sql, new RatingRowMapper(), film.getMpa().getId());
                film.setMpa(fullRating);
            } catch (Exception e) {
                System.err.println("Rating not found for id: " + film.getMpa().getId());
            }
        }
    }

    public Set<Long> getLikes(long filmId) {
        List<Long> likesList = jdbc.queryForList(GET_LIKES_QUERY, Long.class, filmId);
        return new HashSet<>(likesList);
    }

    public void addLike(long filmId, long userId) {
        jdbc.update(ADD_LIKE_QUERY, userId, filmId);
    }

    public void removeLike(long filmId, long userId) {
        jdbc.update(REMOVE_LIKE_QUERY, filmId, userId);
    }
}