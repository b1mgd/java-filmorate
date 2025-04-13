package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewUserRequest {
    @NotBlank(message = "Неверный формат почты")
    @Email(message = "Неверный формат почты")
    private String email;

    @Pattern(regexp = "^\\S*$", message = "Логин не должен содержать пробелы")
    private String login;
    private String name;

    @Past(message = "Некорректная дата рождения")
    private LocalDate birthday;
}
