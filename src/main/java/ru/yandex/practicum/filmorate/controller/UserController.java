package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public ResponseEntity<Collection<User>> getAllUsers() {
        log.info("Получен запрос на вывод списка пользователей: {}", users.values());
        return ResponseEntity.status(HttpStatus.OK).body(users.values());
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User newUser) {
        log.info("Получен запрос на добавление пользователя: {}", newUser);
        Integer id = getNextInt();
        newUser.setId(id);

        if (isBlankName(newUser)) {
            newUser.setName(newUser.getLogin());
            log.info("Логин пользователя c ID {} {} указан в качестве имени", newUser.getId(), newUser.getLogin());
        }

        users.put(id, newUser);
        log.info("Пользователь {} добавлен в список", newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя {}", newUser);

        if (newUser.getId() == null || !users.containsKey(newUser.getId())) {
            log.warn("Пользователь с указанным ID не найден");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(newUser);
        }

        User user = users.get(newUser.getId());

        if (isBlankName(newUser)) {
            user.setName(newUser.getLogin());
            log.info("Логин пользователя c ID {} {} указан в качестве имени", newUser.getId(), newUser.getLogin());
        } else {
            user.setName(newUser.getName());
        }

        user.setLogin(newUser.getLogin());
        user.setEmail(newUser.getEmail());
        user.setBirthday(newUser.getBirthday());
        log.info("Информация о пользователе обновлена: {}", user);

        return ResponseEntity.status(HttpStatus.OK).body(newUser);
    }

    private Integer getNextInt() {
        int currentId = users.keySet()
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
