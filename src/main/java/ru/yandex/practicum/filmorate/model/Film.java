package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private Integer id;

    @NotNull(message = "Название фильма должно быть заполнено")
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @NotNull (message = "Описание фильма должно быть заполнено")
    @NotBlank (message = "Описание фильма не должно быть пустым")
    private String description;

    @NotNull(message = "Дата выхода фильма должна быть указана")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность фильма должна быть заполнена")
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private long duration;
}
