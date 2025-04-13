package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenreMapper {

    public static Genre mapToGenre(GenreDto dto) {
        if (dto == null)
            return null;

        Genre genre = new Genre();
        genre.setId(dto.getId());
        genre.setName(dto.getName());

        return genre;
    }

    public static GenreDto mapToGenreDto(Genre genre) {
        if (genre == null)
            return null;

        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());

        return dto;
    }

    public static List<GenreDto> mapToGenreDtoList(Set<Genre> genres) {
        if (genres == null || genres.isEmpty())
            return Collections.emptyList();

        return genres.stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }
}
