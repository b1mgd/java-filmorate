package ru.yandex.practicum.filmorate.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnBadRequestForEmptyEmail() throws Exception {
        String json = "{\n" +
                "  \"email\": \"\",\n" +
                "  \"login\": \"validLogin\",\n" +
                "  \"birthday\": \"2000-01-01\"\n" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForLoginWithSpaces() throws Exception {
        String json = "{\n" +
                "  \"email\": \"test@example.com\",\n" +
                "  \"login\": \"invalid login\",\n" +
                "  \"birthday\": \"2000-01-01\"\n" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForFutureBirthday() throws Exception {
        String json = "{\n" +
                "  \"email\": \"test@example.com\",\n" +
                "  \"login\": \"validLogin\",\n" +
                "  \"birthday\": \"2100-01-01\"\n" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateUserWithValidData() throws Exception {
        String json = "{\n" +
                "  \"email\": \"test@example.com\",\n" +
                "  \"login\": \"validLogin\",\n" +
                "  \"name\": \"Test User\",\n" +
                "  \"birthday\": \"2000-01-01\"\n" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnNotFoundForUpdatingNonExistentUser() throws Exception {
        String json = "{\n" +
                "  \"id\": 999,\n" +
                "  \"email\": \"test@example.com\",\n" +
                "  \"login\": \"validLogin\",\n" +
                "  \"birthday\": \"2000-01-01\"\n" +
                "}";

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }
}
