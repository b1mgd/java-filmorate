package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Qualifier("userRepository")
public class UserRepository extends BaseRepository<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, " +
            "birthday = ? WHERE user_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE user_id = ?";
    private static final String GET_FRIENDS_QUERY = "SELECT * FROM users u " +
            "JOIN friendship f ON u.user_id = f.friend_id WHERE f.user_id = ?";
    private static final String ADD_FRIEND_QUERY = "INSERT INTO friendship (user_id, friend_id, status) " +
            "VALUES (?, ?, ?)";
    private static final String REMOVE_FRIEND_QUERY = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> rowMapper) {
        super(jdbc, rowMapper);
    }


    @Override
    public List<User> getAllUsers() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    @Override
    public User createUser(User newUser) {
        int id = insert(
                INSERT_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday()
        );

        newUser.setId(id);

        return newUser;
    }

    @Override
    public User updateUser(User updatedUser) {
        update(
                UPDATE_QUERY,
                updatedUser.getEmail(),
                updatedUser.getLogin(),
                updatedUser.getName(),
                updatedUser.getBirthday(),
                updatedUser.getId()
        );

        return updatedUser;
    }

    @Override
    public boolean deleteUser(Integer id) {
        if (delete(DELETE_QUERY, id)) {
            log.info("Пользователь с id {} удален", id);
            return true;
        } else {
            log.warn("Не удалось удалить пользователя с id {}", id);
            return false;
        }
    }

    @Override
    public List<User> getFriends(Integer id) {
        List<User> friends = findMany(GET_FRIENDS_QUERY, id);

        if (friends.isEmpty()) {
            log.warn("Друзья пользователя {} не были найдены", id);
        } else {
            log.info("Найдены друзья пользователя {}", id);
        }

        return friends;
    }

    @Override
    public boolean addFriend(Integer userId, Integer friendId) {
        int rowsAdded = jdbc.update(ADD_FRIEND_QUERY, userId, friendId, true);

        if (rowsAdded == 0) {
            log.warn("Пользователь с id: {} не смог добавить в друзья пользователя {}", userId, friendId);
        } else {
            log.info("Пользователь с id: {} добавил в друзья пользователя {}", userId, friendId);
        }

        return rowsAdded != 0;
    }


    @Override
    public boolean removeFriend(Integer userId, Integer friendId) {
        int rowsDeleted = jdbc.update(REMOVE_FRIEND_QUERY, userId, friendId);
        boolean removeFriendResult = rowsDeleted != 0;

        if (!removeFriendResult) {
            log.warn("Друг с id {} не был удален у пользователя {}", friendId, userId);
        } else {
            log.info("Друг с id {} был удален из друзей у пользователя {}", friendId, userId);
        }

        return removeFriendResult;
    }
}
