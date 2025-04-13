package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private MpaDto mpa;
    private List<GenreDto> genres = new ArrayList<>();
    private Set<Integer> likes = new LinkedHashSet<>();
}
