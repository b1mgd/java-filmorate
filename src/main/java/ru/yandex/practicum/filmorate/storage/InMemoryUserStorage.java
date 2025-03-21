package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        log.info("Направлен список пользователей: {}", users.values());
        return List.copyOf(users.values());
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        log.info("По id {} найден пользователь {}", id, users.get(id));
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Set<Integer> getUserIds() {
        log.info("Направлено множество id пользователей: {}", users.keySet());
        return Set.copyOf(users.keySet());
    }

    @Override
    public void addUser(User newUser) {
        users.put(newUser.getId(), newUser);
        log.info("Пользователь {} добавлен в хранилище под id {}", newUser, newUser.getId());
    }

    @Override
    public void updateUser(User newUser) {
        if (!users.containsKey(newUser.getId())) {
            log.warn("Попытка несуществующего пользователя с id {}", newUser.getId());
            throw new NotFoundException("Пользователь с id " + newUser.getId() + " не найден");
        }

        users.put(newUser.getId(), newUser);
        log.info("Пользователь {} с id {} обновлен в хранилище", newUser, newUser.getId());
    }
}
