package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class BaseRepository<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> rowMapper;

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, rowMapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, rowMapper, params);
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);

        if (rowsUpdated == 0) {
            log.warn("Данные по запросу {} не были обновлены", query);
            throw new InternalServerException("Не удалось обновить данные");
        } else {
            log.info("Данные по запросу {} были обновлены", query);
        }
    }

    protected int insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            setParameters(ps, params);
            return ps;
        }, keyHolder);

        return Optional.ofNullable(keyHolder.getKey())
                .map(key -> (Integer) key)
                .orElseThrow(() -> {
                    log.warn("Не удалось добавить данные по запросу {}", query);
                    return new InternalServerException("Не удалось сохранить данные");
                });
    }

    protected boolean delete(String query, int id) {
        int rowsDeleted = jdbc.update(query, id);
        if (rowsDeleted == 0) {
            log.warn("Не удалось удалить данные по запросу {}", query);
            throw new InternalServerException("Не удалось удалить данные по запросу");
        }

        return true;
    }

    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }
}
