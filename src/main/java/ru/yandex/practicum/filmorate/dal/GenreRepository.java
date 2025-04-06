package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepository extends BaseRepository<Genre> {
    private static final String GET_GENRES = "SELECT * FROM genre";
    private static final String GET_GENRE_BY_ID = "SELECT * FROM genre WHERE genre_id = ?";


    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> findAll() {
        return findMany(GET_GENRES);
    }

    public Optional<Genre> findOne(int id) {
        return findOne(GET_GENRE_BY_ID, id);
    }

}
