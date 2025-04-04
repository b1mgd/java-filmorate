package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    public Collection<User> getFriends(Integer id) {
        User user = getUserById(id);
        return user.getFriends()
                .stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Integer id, Integer otherId) {
        Set<User> userFriends = new HashSet<>(getFriends(id));

        return getFriends(otherId).stream()
                .filter(userFriends::contains)
                .collect(Collectors.toList());
    }

    public User addUser(User newUser) {
        newUser.setId(getNextInt());

        if (isBlankName(newUser)) {
            newUser.setName(newUser.getLogin());
            log.info("Логин пользователя c ID {} {} указан в качестве имени", newUser.getId(), newUser.getLogin());
        }

        userStorage.addUser(newUser);

        return newUser;
    }

    public User updateUser(User newUser) {

        if (newUser.getId() == null || !userStorage.getUserIds().contains(newUser.getId())) {
            log.warn("Пользователь с указанным id {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с указанным id " + newUser.getId() + " не найден");
        }

        if (isBlankName(newUser)) {
            newUser.setName(newUser.getLogin());
            log.info("Логин пользователя {} c id {} указан в качестве имени", newUser.getId(), newUser.getLogin());
        }

        userStorage.updateUser(newUser);

        return newUser;
    }

    public void addFriend(Integer id, Integer friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);

        if (user.getFriends().contains(friendId)) {
            log.warn("Пользователь {} уже добавил в друзья {}", id, friendId);
            throw new ValidateException("Пользователи с id " + id + " и " + friendId + " уже являются друзьями");
        }

        user.addFriend(friendId);
        friend.addFriend(id);

        log.info("Пользователи с id {} и {} стали друзьями", id, friendId);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);

        if (!user.getFriends().contains(friendId))
            return;

        user.deleteFriend(friendId);
        friend.deleteFriend(id);

        log.info("Запрос на удаление из друзей пользователей {} и {} выполнен", id, friendId);
    }

    private Integer getNextInt() {
        int currentId = userStorage.getUserIds()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentId;
    }

    private boolean isBlankName(User newUser) {
        return newUser.getName() == null || newUser.getName().isBlank();
    }
}
