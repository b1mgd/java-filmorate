package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, rating_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";

    public FilmRepository(JdbcTemplate jdbc, FilmRowMapper mapper) {
        super(jdbc, mapper);
    }

    public List<Film> findAll(){
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Film> findById(long id){
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Film save(Film film){
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRating()
        );
        film.setId(id);
        return film;
    }

    public Film update(Film film){
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRating(),
                film.getId()
        );
        return film;
    }

    public Set<Long> getLikes(long filmId) {
        String sql = "SELECT user_id FROM Likes WHERE film_id = ?";
        List<Long> likesList = jdbc.queryForList(sql, Long.class, filmId);
        return new HashSet<>(likesList);
    }

    public void addLike(long filmId, long userId) {
        String sql = "INSERT INTO Likes (user_id, film_id) VALUES (?, ?)";
        update(sql, userId, filmId);
    }

    public void removeLike(long filmId, long userId) {
        String sql = "DELETE FROM Likes WHERE film_id = ? AND user_id = ?";
        update(sql, filmId, userId);
    }
}
