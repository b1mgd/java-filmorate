package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {

    Collection<User> getAllUsers();

    Optional<User> getUserById(Integer id);

    Set<Integer> getUserIds();

    void addUser(User newUser);

    void updateUser(User newUser);
}
