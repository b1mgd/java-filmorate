package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class User {
    private Integer id;

    @NotBlank(message = "Неверный формат почты")
    @Email(message = "Неверный формат почты")
    private String email;

    @Pattern(regexp = "^\\S*$", message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    @Past (message = "Некорректная дата рождения")
    private LocalDate birthday;

    private final Set<Integer> friends = new HashSet<>();
}
