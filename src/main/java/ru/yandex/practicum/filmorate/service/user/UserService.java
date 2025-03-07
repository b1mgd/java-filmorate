package ru.yandex.practicum.filmorate.service.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.EmptyFieldException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired // Внедрение зависимостей через конструктор
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public void addFriend(int firstUserId, int secondUserId) {
        userIsExists(firstUserId);
        userIsExists(secondUserId);
        if (!inMemoryUserStorage.getUserById(firstUserId).getFriends().contains(secondUserId) &&
                !inMemoryUserStorage.getUserById(secondUserId).getFriends().contains(firstUserId)) {
            log.debug("add friends");
            inMemoryUserStorage.getUserById(firstUserId).getFriends().add(secondUserId);
            inMemoryUserStorage.getUserById(secondUserId).getFriends().add(firstUserId);
        } else if (inMemoryUserStorage.getUserById(firstUserId).getFriends().contains(secondUserId) &&
                inMemoryUserStorage.getUserById(secondUserId).getFriends().contains(firstUserId)) {
            log.warn("the friendship was already created");
            throw new RuntimeException("the friendship was already created");
        } else {
            log.warn("friendship can't be created");
            throw new RuntimeException("friendship can't be created");
        }

    }

    public void deleteFriend(int firstUserId, int secondUserId) {
        userIsExists(firstUserId);
        userIsExists(secondUserId);
        if (inMemoryUserStorage.getUserById(firstUserId).getFriends().contains(secondUserId) &&
                inMemoryUserStorage.getUserById(secondUserId).getFriends().contains(firstUserId)) {
            log.debug("delete friends");
            inMemoryUserStorage.getUserById(firstUserId).getFriends().remove(secondUserId);
            inMemoryUserStorage.getUserById(secondUserId).getFriends().remove(firstUserId);
        } else if (!inMemoryUserStorage.getUserById(firstUserId).getFriends().contains(secondUserId) &&
                !inMemoryUserStorage.getUserById(secondUserId).getFriends().contains(firstUserId)) {
            inMemoryUserStorage.getUserById(firstUserId).getFriends().remove(secondUserId);
            inMemoryUserStorage.getUserById(secondUserId).getFriends().remove(firstUserId);
        } else {
            log.warn("friend can't be deleted");
            throw new NotFoundException("friend can't be deleted");
        }

    }

    public List<User> getFriends(int userId) {
        userIsExists(userId);
        List<User> friends = new ArrayList<>();
        for (Integer friendId : inMemoryUserStorage.getUserById(userId).getFriends()) {
            User friend = inMemoryUserStorage.getUserById(friendId);
            if (friend != null) {
                friends.add(friend);
            }
        }
        return friends;
    }

    public List<User> getGeneralFriends(int firstUserId, int secondUserId) {
        userIsExists(firstUserId);
        userIsExists(secondUserId);
        Set<Integer> firstUserFriends = inMemoryUserStorage.getUserById(firstUserId).getFriends();

        Set<Integer> secondUserFriends = inMemoryUserStorage.getUserById(secondUserId).getFriends();

        firstUserFriends.retainAll(secondUserFriends);

        List<User> commonFriends = new ArrayList<>();
        for (Integer friendId : firstUserFriends) {
            User friend = inMemoryUserStorage.getUserById(friendId);
            if (friend != null) {
                commonFriends.add(friend);
            }
        }
        return commonFriends;
    }


    public Collection<User> getUsers() {
        return inMemoryUserStorage.getUsers();
    }

    public User addUser(User user) {
        if (user.getName() == null) {
            log.debug("Username is empty, using login as name");
            user.setName(user.getLogin());
        }
        return inMemoryUserStorage.addUser(user);
    }

    public User updateUser(@RequestBody @Valid User user) {
        if (user.getId() == 0) {
            log.warn("User id is empty");
            throw new EmptyFieldException("ID can't be empty");
        }
        return inMemoryUserStorage.updateUser(user);
    }

    public User getUserById(@PathVariable int id) {
        log.info("getUserById");
        return inMemoryUserStorage.getUserById(id);
    }


    private void userIsExists(int userId) {
        if (inMemoryUserStorage.getUserById(userId) == null) {
            throw new NotFoundException("пользователя с заданным id не существует");
        }
    }
}
