package ru.yandex.practicum.filmorate.mappers;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class GenreMapper {
    public static GenreDto mapToGenreDto(Genre genre) {
        if (genre == null) return null;
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

    public static List<GenreDto> mapToGenreDtoList(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return Collections.emptyList();
        }
        return genres.stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }

    public static Genre mapToGenre(GenreDto dto) {
        if (dto == null) return null;
        Genre genre = new Genre();
        genre.setId(dto.getId());
        genre.setName(dto.getName());
        return genre;
    }

    public static List<Genre> mapToGenreList(List<GenreDto> genreDtos) { // Принимает и возвращает List
        if (genreDtos == null || genreDtos.isEmpty()) {
            return Collections.emptyList();
        }
        return genreDtos.stream()
                .map(GenreMapper::mapToGenre)
                .collect(Collectors.toList()); // Собираем в List
    }

}