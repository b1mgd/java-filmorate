package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class Film {
    private Integer id;

    @NotBlank(message = "Некорректное название фильма")
    private String name;

    @NotNull(message = "Описание фильма должно быть заполнено")
    @NotBlank(message = "Некорректное описание фильма")
    private String description;

    @NotNull(message = "Дата выхода фильма должна быть указана")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
    private Mpa mpa;
    private Set<Genre> genres = new LinkedHashSet<>();
}
