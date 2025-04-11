package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MpaMapper {

    public static Mpa mapToMpa(MpaDto dto) {
        if (dto == null)
            return null;

        Mpa mpa = new Mpa();
        mpa.setId(dto.getId());
        mpa.setRating(dto.getName());

        return mpa;
    }

    public static MpaDto maptoMpaDto(Mpa mpa) {
        if (mpa == null)
            return null;

        MpaDto dto = new MpaDto();
        dto.setId(mpa.getId());
        dto.setName(mpa.getRating());

        return dto;
    }
}
