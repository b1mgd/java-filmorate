package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.RatingRowMapper;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

@Repository
public class RatingRepository extends BaseRepository<Rating> {
    private static final String GET_RATINGS = "SELECT rating_id, rating_name FROM rating ORDER BY rating_id";
    private static final String GET_RATING_BY_ID = "SELECT rating_id, rating_name FROM rating WHERE rating_id = ?";

    public RatingRepository(JdbcTemplate jdbc, RatingRowMapper mapper) {
        super(jdbc, mapper);
    }

    public List<Rating> findAll() {
        return findMany(GET_RATINGS);
    }

    public Optional<Rating> findById(int id) {
        return findOne(GET_RATING_BY_ID, id);
    }
}