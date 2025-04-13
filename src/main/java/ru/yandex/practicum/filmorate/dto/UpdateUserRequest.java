package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    private Integer id;

    @NotBlank(message = "Неверный формат почты")
    @Email(message = "Неверный формат почты")
    private String email;

    @Pattern(regexp = "^\\S*$", message = "Логин не должен содержать пробелы")
    private String login;
    private String name;

    @Past(message = "Некорректная дата рождения")
    private LocalDate birthday;

    public boolean hasUserName() {
        return name != null && !name.isBlank();
    }

    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }

    public boolean hasLogin() {
        return login != null && !login.isBlank();
    }

    public boolean hasBirthday() {
        return birthday != null;
    }
}
