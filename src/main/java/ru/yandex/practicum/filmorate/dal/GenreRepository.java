package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepository extends BaseRepository<Genre> {

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> rowMapper) {
        super(jdbc, rowMapper);
    }

    public List<Genre> getGenres() {
        String query = "SELECT * FROM genre ORDER BY genre_id ASC;";
        return findMany(query);
    }

    public Optional<Genre> getGenreById(Integer genreId) {
        String query = "SELECT * FROM genre WHERE genre_id = ?;";
        return findOne(query, genreId);
    }

    public boolean saveGenre(Integer mpaId, Integer filmId) {
        String query = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?);";
        int rowAdded = jdbc.update(query, mpaId, filmId);

        return rowAdded != 0;
    }
}
