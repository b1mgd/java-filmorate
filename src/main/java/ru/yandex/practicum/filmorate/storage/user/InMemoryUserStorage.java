package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        user.setId(setUserId());
        log.debug("User id: {}", user.getId());
        users.put(user.getId(), user);
        log.info("Added user: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            if (user.getName() != null && !user.getName().isBlank()) {
                log.debug("Updating user with name {}", user.getName());
                oldUser.setName(user.getName());
            }

            if (user.getBirthday() != null) {
                log.debug("Updating user with birthday {}", user.getBirthday());
                oldUser.setBirthday(user.getBirthday());
            }

            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                log.debug("Updating user with email {}", user.getEmail());
                oldUser.setEmail(user.getEmail());
            }

            if (user.getLogin() != null && !user.getLogin().isBlank()) {
                if (user.getLogin().contains(" ")) {
                    log.warn("Invalid login");
                    throw new MethodArgumentNotValidException("Invalid login");
                }
                log.debug("Updating user with login {}", user.getLogin());
                oldUser.setLogin(user.getLogin());
            }
            log.info("User updated");
            return oldUser;
        }

        log.warn("User not found");
        throw new NotFoundException("User not found");
    }

    @Override
    public int setUserId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public User getUserById(int userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            log.warn("User not found");
            throw new NotFoundException("Пользователя с указанным id не существует");
        }
    }
}
