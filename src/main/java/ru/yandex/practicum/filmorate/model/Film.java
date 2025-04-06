package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class Film {
    private long id;
    @NotBlank(message = "Film name is empty")
    private String name;
    @Size(max = 200, message = "Film description is too long")
    private String description;
    private LocalDate releaseDate;
    @Min(value = 0, message = "Film duration is negative")
    private int duration;
    private Rating rating;
    private Set<Genre> genres;
}
