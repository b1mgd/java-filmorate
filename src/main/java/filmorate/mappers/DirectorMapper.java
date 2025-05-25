package filmorate.mappers;

import lombok.experimental.UtilityClass;
import filmorate.dto.DirectorDto;
import filmorate.model.Director;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class DirectorMapper {
    public static Director mapToDirector(Director request) {
        Director director = new Director();
        director.setId(request.getId());
        director.setName(request.getName());
        return director;
    }

    public static DirectorDto mapToDirectorDto(Director director) {
        DirectorDto directorDto = new DirectorDto();
        directorDto.setId(director.getId());
        directorDto.setName(director.getName());
        return directorDto;
    }

    public static Director toDirectorDTO(DirectorDto directorDto) {
        Director director = new Director();
        director.setId(directorDto.getId());
        director.setName(directorDto.getName());
        return director;
    }

    public static List<DirectorDto> mapToDirectorDtoList(List<Director> directors) {
        if (directors == null || directors.isEmpty()) {
            return Collections.emptyList();
        }
        return directors.stream()
                .map(DirectorMapper::mapToDirectorDto)
                .collect(Collectors.toList());
    }

    public static List<Director> mapToDirectorList(List<DirectorDto> directorDtos) {
        if (directorDtos == null || directorDtos.isEmpty()) {
            return Collections.emptyList();
        }
        return directorDtos.stream()
                .map(DirectorMapper::toDirectorDTO)
                .collect(Collectors.toList());
    }

}
