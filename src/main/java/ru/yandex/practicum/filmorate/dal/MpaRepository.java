package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRepository extends BaseRepository<Mpa> {

    public MpaRepository(JdbcTemplate jdbc, RowMapper<Mpa> rowMapper) {
        super(jdbc, rowMapper);
    }

    public List<Mpa> getMpas() {
        String query = "SELECT * FROM mpa ORDER BY mpa_id ASC";
        return findMany(query);
    }

    public Optional<Mpa> getMpaById(Integer mpaId) {
        String query = "SELECT * FROM mpa WHERE mpa_id = ?";
        return findOne(query, mpaId);
    }
}
