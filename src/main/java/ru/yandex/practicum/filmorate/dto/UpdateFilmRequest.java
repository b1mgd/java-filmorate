package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;

@Data
public class UpdateFilmRequest {
    private Integer id;

    @NotBlank(message = "Некорректное название фильма")
    private String name;

    @NotNull(message = "Описание фильма должно быть заполнено")
    @NotBlank(message = "Некорректное описание фильма")
    private String description;

    @NotNull(message = "Дата выхода фильма должна быть указана")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;
    private Mpa mpa;

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return duration != null;
    }

    public boolean hasMpa() {
        return mpa != null;
    }
}
