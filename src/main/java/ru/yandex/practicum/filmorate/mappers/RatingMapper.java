package ru.yandex.practicum.filmorate.mappers;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.model.Rating;

@UtilityClass
public class RatingMapper {
    public static RatingDto mapToRatingDto(Rating rating) {
        if (rating == null) return null;
        RatingDto dto = new RatingDto();
        dto.setId(rating.getId());
        dto.setName(rating.getName());
        return dto;
    }

    public static Rating mapToRating(RatingDto dto) {
        if (dto == null) return null;
        Rating rating = new Rating();
        rating.setId(dto.getId());
        rating.setName(dto.getName());
        return rating;
    }

}