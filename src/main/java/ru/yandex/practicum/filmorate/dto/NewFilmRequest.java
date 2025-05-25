package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class NewFilmRequest {
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
    private Set<Genre> genres = new HashSet<>();
}
