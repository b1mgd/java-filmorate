package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collections;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {

    public static Film maptoFilm(NewFilmRequest request) {
        Film film = new Film();

        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());

        if (request.getMpa() != null) {
            film.setMpa(request.getMpa());
        } else {
            film.setMpa(null);
        }

        if (request.getGenres() != null) {
           film.setGenres(request.getGenres());
        } else {
            film.setGenres(Collections.emptySet());
        }

        return film;
    }

    public static FilmDto maptoFilmDto(Film film, Set<Integer> likes) {
        FilmDto filmDto = new FilmDto();

        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDuration(film.getDuration());
        filmDto.setMpa(MpaMapper.maptoMpaDto(film.getMpa()));
        filmDto.setGenres(GenreMapper.mapToGenreDtoList(film.getGenres()));

        if (likes != null && !likes.isEmpty()) {
            filmDto.setLikes(likes);
        } else {
            filmDto.setLikes(Collections.emptySet());
        }

        return filmDto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }
        if (request.hasMpa() && request.getMpa() != null) {
            film.setMpa(request.getMpa());
        }

        return film;
    }
}
