package filmorate.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class NewUserRequest {
    private long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}
