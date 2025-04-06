package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

@Repository
public class RatingRepository extends BaseRepository<Rating> {
    private static final String GET_RATING = "SELECT * FROM rating";
    private static final String GET_RATING_BY_ID = "SELECT * FROM rating WHERE id = ?";


    public RatingRepository(JdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    public List<Rating> findAll() {
        return findMany(GET_RATING);
    }

    public Optional<Rating> findOne(int id) {
        return findOne(GET_RATING_BY_ID, id);
    }

}