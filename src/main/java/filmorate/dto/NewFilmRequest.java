package filmorate.dto;

import lombok.Data;
import filmorate.model.Director;
import filmorate.model.Rating;

@Data
public class NewFilmRequest {
    private String name;
    private String description;
    private String releaseDate;
    private String duration;
    private Rating ratingId;
    private Director directors;
}
