package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Repository
public class FeedRepository {

    private final JdbcTemplate jdbc;
    private final EventRowMapper eventRowMapper;

    public FeedRepository(JdbcTemplate jdbc, EventRowMapper eventRowMapper) {
        this.jdbc = jdbc;
        this.eventRowMapper = eventRowMapper;
    }

    public List<Event> getUsersEventFeed(long userId) {
        String sqlQuery = "SELECT * FROM event_feed WHERE user_id = ?;";
        return jdbc.query(sqlQuery, eventRowMapper, userId);
    }

    public void saveEvent(Event event) {
        String sqlQuery = "INSERT INTO event_feed (timestamp, user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?, ?);";
        jdbc.update(sqlQuery, event.getTimestamp(), event.getUserId(), event.getEventType().name(),
                event.getOperation().name(), event.getEntityId());
    }
}
