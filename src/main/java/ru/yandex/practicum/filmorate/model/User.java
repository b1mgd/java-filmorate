package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class User {
    private Integer id;

    @NotBlank(message = "Неверный формат почты")
    @Email(message = "Неверный формат почты")
    private String email;

    @Pattern(regexp = "^\\S*$", message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    @Past (message = "Некорректная дата рождения")
    private LocalDate birthday;

    private final Map<Integer, FriendshipStatus> friends = new HashMap<>();

    public Collection<Integer> getFriends() {
        return List.copyOf(friends.keySet());
    }

    // временная реализация методов до спринта 12
    public void addFriend(Integer id) {
        friends.put(id, FriendshipStatus.ACCEPTED);
    }

    public void deleteFriend(Integer id) {
        friends.remove(id);
    }
}
