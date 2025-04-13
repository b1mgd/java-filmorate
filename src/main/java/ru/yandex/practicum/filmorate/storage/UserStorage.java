package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> getAllUsers();

    Optional<User> getUserById(Integer id);

    Optional<User> getUserByEmail(String email);

    User createUser(User newUser);

    User updateUser(User newUser);

    boolean deleteUser(Integer id);

    List<User> getFriends(Integer id);

    boolean addFriend(Integer userId, Integer friendId);

    boolean removeFriend(Integer userId, Integer friendId);
}
