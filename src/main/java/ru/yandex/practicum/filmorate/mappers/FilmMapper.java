package ru.yandex.practicum.filmorate.mappers;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class FilmMapper {
    public static Film mapToFilm(Film requestFilm) {
        Film film = new Film();
        film.setId(requestFilm.getId());
        film.setName(requestFilm.getName());
        film.setDescription(requestFilm.getDescription());
        film.setReleaseDate(requestFilm.getReleaseDate());
        film.setDuration(requestFilm.getDuration());

        if (requestFilm.getMpa() != null && requestFilm.getMpa().getId() != 0) {
            film.setMpa(requestFilm.getMpa());
        } else {
            film.setMpa(null);
        }

        if (requestFilm.getGenres() != null) {
            Set<Genre> genresWithIdOnly = requestFilm.getGenres().stream()
                    .filter(g -> g != null && g.getId() != 0)
                    .collect(Collectors.toSet());
            film.setGenres(genresWithIdOnly);
        } else {
            film.setGenres(new HashSet<>());
        }

        return film;
    }


    public static FilmDto mapToFilmDto(Film film, Set<Long> likes) {
        if (film == null) return null;
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setMpa(RatingMapper.mapToRatingDto(film.getMpa()));
        dto.setGenres(GenreMapper.mapToGenreDtoSet(film.getGenres()));
        dto.setLikes(Optional.ofNullable(likes).orElse(Collections.emptySet()));
        return dto;
    }

    public static FilmDto mapToFilmDto(Film film) {
        return mapToFilmDto(film, Collections.emptySet());
    }
}