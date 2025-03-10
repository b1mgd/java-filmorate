package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private Integer id;

    @NotNull(message = "Почта не указана")
    @NotBlank(message = "Неверный формат почты")
    @Email(message = "Неверный формат почты")
    private String email;

    @NotNull (message = "Необходимо указать логин")
    @Pattern(regexp = "^\\S*$", message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    @NotNull (message = "Необходимо указать дату рождения")
    @Past (message = "Некорректная дата рождения")
    private LocalDate birthday;
}
