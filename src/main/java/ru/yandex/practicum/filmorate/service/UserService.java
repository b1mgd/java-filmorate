package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userRepository") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<UserDto> getAllUsers() {
        List<UserDto> users = userStorage.getAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
        log.info("Получен список всех зарегистрированных пользователей размером: {}", users.size());

        return users;
    }

    public UserDto getUserById(Integer id) {
        UserDto user = userStorage.getUserById(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> {
                    log.warn("Пользователь c id {} не был найден", id);
                    return new NotFoundException("Пользователь с id " + id + " не найден");
                });
        log.info("Получены данные о пользователе с id {}", id);

        return user;
    }

    public UserDto getUserByEmail(String email) {
        return userStorage.getUserByEmail(email)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> {
                    log.warn("Пользователь с указанной почтой не был найден: {}", email);
                    return new NotFoundException("Пользователь с адресом почты " + email + " не найден");
                });
    }

    public UserDto createUser(NewUserRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            log.warn("Не удалось создать пользователя, т.к. не была указана почта");
            throw new ValidateException("Почта пользователя должна быть указана");
        }

        Optional<User> existingUser = userStorage.getUserByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            log.warn("Пользователь с указанным email уже существует: {}", request.getEmail());
            throw new DuplicatedDataException("Пользователь с указанной почтой уже зарегистрирован");
        } else {
            log.info("Почта: {} не занята другим пользователем", request.getEmail());
        }

        if (isBlankName(request)) {
            log.info("Пользователю установлено имя {}, т.к. оно не было указано", request.getLogin());
            request.setName(request.getLogin());
        }

        User user = UserMapper.mapToUser(request);
        userStorage.createUser(user);
        log.info("Пользователь добавлен в базу: {}", user);

        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(UpdateUserRequest request) {
        User updatedUser = userStorage.getUserById(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> {
                    log.warn("Не удалось обновить информацию о пользователе с id {}", request.getId());
                    return new NotFoundException("Пользователь с id " + request.getId() + " не найден");
                });

        updatedUser = userStorage.updateUser(updatedUser);

        return UserMapper.mapToUserDto(updatedUser);
    }

    public void deleteUser(Integer id) {
        if (userStorage.deleteUser(id)) {
            log.info("Пользователь с id {} удален", id);
        } else {
            log.warn("Не удалось удалить пользователя с id {}", id);
            throw new InternalServerException("Не удалось удалить пользователя из системы");
        }
    }

    public List<UserDto> getFriends(Integer id) {
        getUserById(id);

        List<UserDto> friends = userStorage.getFriends(id).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());

            log.info("Получен список друзей пользователя с id {}: {}", id, friends);

        return friends;
    }

    public List<UserDto> getCommonFriends(Integer id, Integer otherId) {
        Set<User> otherUserFriends = new HashSet<>(userStorage.getFriends(otherId));
        List<UserDto> commonFriends = userStorage.getFriends(id).stream()
                .filter(otherUserFriends::contains)
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());

        if (commonFriends.isEmpty()) {
            log.info("Пользователи с id {} и {} не имеют общих друзей", id, otherId);
        } else {
            log.info("Получен список общих друзей пользователей с id {} и {}", id, otherId);
        }

        return commonFriends;
    }

    public void addFriend(Integer userId, Integer friendId) {
        getUserById(userId);
        getUserById(friendId);

        if (areFriends(userId, friendId)) {
            log.warn("Пользователь {} уже добавил в друзья {}", userId, friendId);
            throw new ValidateException("Пользователи с userId " + userId + " и " + friendId + " уже являются друзьями");
        }

        if (userStorage.addFriend(friendId, userId)) {
            log.info("Пользователи с userId {} и {} стали друзьями", userId, friendId);
        } else {
            log.warn("Не удалось добавить пользователя {} в друзья", friendId);
            throw new InternalServerException("Не удалось добавить пользователя " + friendId + " в друзья");
        }
    }

    public void removeFriend(Integer userId, Integer friendId) {
        getUserById(userId);
        getUserById(friendId);

        if (!areFriends(userId, friendId)) {
            log.info("Пользователи с userId {} и {} не являются друзьями. Удаление не требуется", userId, friendId);
            return;
        }

        if (userStorage.removeFriend(userId, friendId)) {
            log.info("Запрос на удаление из друзей пользователей {} и {} выполнен", userId, friendId);
        } else {
            throw new InternalServerException("Не удалось удалить пользователей "
                    + userId + " и " + friendId + " из друзей");
        }
    }

    private boolean isBlankName(NewUserRequest request) {
        boolean isBlank = request.getName() == null || request.getName().isBlank();

        if (isBlank) {
            log.debug("Имя в запросе на добавление пользователя указано: {}", request);
        } else {
            log.debug("Имя в запросе на добавление пользователя не было предоставлено: {}", request);
        }

        return isBlank;
    }

    private boolean areFriends(Integer id, Integer otherId) {
        boolean areFriends = userStorage.getFriends(id)
                .stream()
                .anyMatch(friend -> friend.getId().equals(otherId));

        if (!areFriends) {
            log.debug("Пользователи {}, {} не являются друзьями", id, otherId);
        } else {
            log.debug("Пользователи {}, {} находятся в друзьях", id, otherId);
        }

        return areFriends;
    }
}
