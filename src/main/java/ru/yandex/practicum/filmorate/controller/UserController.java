package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Получен запрос на вывод списка пользователей");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Integer id) {
        log.info("Получен запрос на вывод пользователя с id {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        log.info("Получен запрос на вывод пользователя с email {}", email);
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public ResponseEntity<List<UserDto>> getCommonFriends(@PathVariable Integer id,
                                                          @PathVariable Integer otherId) {
        log.info("Получен запрос на вывод общих друзей пользователей с id {} и {}", id, otherId);
        return ResponseEntity.ok(userService.getCommonFriends(id, otherId));
    }

    @PostMapping
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody NewUserRequest request) {
        log.info("Получен запрос на добавление пользователя: {}", request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @PutMapping
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        log.info("Получен запрос на обновление пользователя: {}", request);
        return ResponseEntity.ok(userService.updateUser(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        log.info("Получен запрос на удаление пользователя с id {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{id}/friends")
    public ResponseEntity<List<UserDto>> getFriends(@PathVariable Integer id) {
        log.info("Получен запрос на вывод списка друзей пользователя с id {}", id);
        return ResponseEntity.ok(userService.getFriends(id));
    }

    @PutMapping("{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable Integer id,
                                             @PathVariable Integer friendId) {
        log.info("Получен запрос на добавление пользователем с id {} друга с id {}", id, friendId);
        userService.addFriend(friendId, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Integer id,
                                             @PathVariable Integer friendId) {
        log.info("Получен запрос от пользователя c id {} на удаление из списка друзей друга с id {}", id, friendId);
        userService.removeFriend(id, friendId);
        return ResponseEntity.ok().build();
    }
}
