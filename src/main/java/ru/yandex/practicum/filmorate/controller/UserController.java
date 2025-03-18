package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Collection<User>> getAllUsers() {
        log.info("Получен запрос на вывод списка пользователей");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        log.info("Получен запрос на вывод пользователя с id {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("{id}/friends")
    public ResponseEntity<Collection<User>> getFriends(@PathVariable Integer id) {
        log.info("Получен запрос на вывод списка друзей пользователя с id {}", id);
        return ResponseEntity.ok(userService.getFriends(id));
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public ResponseEntity<Collection<User>> getCommonFriends(@PathVariable Integer id,
                                                             @PathVariable Integer otherId) {
        log.info("Получен запрос на вывод общих друзей пользователей с id {} и {}", id, otherId);
        return ResponseEntity.ok(userService.getCommonFriends(id, otherId));
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User newUser) {
        log.info("Получен запрос на добавление пользователя: {}", newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(newUser));
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя {}", newUser);
        return ResponseEntity.ok(userService.updateUser(newUser));
    }

    @PutMapping("{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable Integer id,
                                          @PathVariable Integer friendId) {
        log.info("Получен запрос на добавление пользователем с id {} друга с id {}", id, friendId);
        userService.addFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Integer id,
                                             @PathVariable Integer friendId) {
        log.info("Получен запрос на удаление из списка друзей пользователя c id {} друга с id {}", id, friendId);
        userService.deleteFriend(id, friendId);
        return ResponseEntity.ok().build();
    }
}
