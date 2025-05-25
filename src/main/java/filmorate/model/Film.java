package filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class Film {
    private long id;
    @NotBlank(message = "Film name is empty")
    private String name;
    @Size(max = 200, message = "Film description is too long (max 200 chars)")
    private String description;
    private LocalDate releaseDate;
    @Min(value = 1, message = "Film duration must be positive")
    private int duration;
    @NotNull(message = "Rating cannot be null")
    private Rating mpa;
    private List<Genre> genres = new ArrayList<>();
    private List<Director> directors = new ArrayList<>();
}
