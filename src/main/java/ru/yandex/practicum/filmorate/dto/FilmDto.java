package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class FilmDto {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private RatingDto mpa;
    private Set<GenreDto> genres = new HashSet<>();
    private Set<Long> likes = new HashSet<>();
}