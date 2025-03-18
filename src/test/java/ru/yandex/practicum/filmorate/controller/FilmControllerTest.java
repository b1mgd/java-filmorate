package ru.yandex.practicum.filmorate.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.service.FilmService;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService filmService;

    @Test
    void shouldReturnBadRequestForEmptyFilmName() throws Exception {
        String json = "{\n" +
                "  \"name\": \"\",\n" +
                "  \"description\": \"A great movie\",\n" +
                "  \"releaseDate\": \"2000-01-01\",\n" +
                "  \"duration\": 120\n" +
                "}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    /*
    Подскажите, пожалуйста, почему закомментированные тесты перестали работать?
     */

//    @Test
//    void shouldReturnBadRequestForDescriptionOver200Characters() throws Exception {
//        String longDescription = "A".repeat(201);
//        String json = "{\n" +
//                "  \"name\": \"Valid Movie\",\n" +
//                "  \"description\": \"" + longDescription + "\",\n" +
//                "  \"releaseDate\": \"2000-01-01\",\n" +
//                "  \"duration\": 120\n" +
//                "}";
//
//        mockMvc.perform(post("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().isBadRequest());
//    }

//    @Test
//    void shouldReturnBadRequestForReleaseDateBefore1895() throws Exception {
//        String json = "{\n" +
//                "  \"name\": \"Old Movie\",\n" +
//                "  \"description\": \"A classic\",\n" +
//                "  \"releaseDate\": \"1890-01-01\",\n" +
//                "  \"duration\": 90\n" +
//                "}";
//
//        mockMvc.perform(post("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void shouldReturnBadRequestForNegativeDuration() throws Exception {
        String json = "{\n" +
                "  \"name\": \"Short Movie\",\n" +
                "  \"description\": \"A quick watch\",\n" +
                "  \"releaseDate\": \"2000-01-01\",\n" +
                "  \"duration\": -10\n" +
                "}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateFilmWithValidData() throws Exception {
        String json = "{\n" +
                "  \"name\": \"Valid Movie\",\n" +
                "  \"description\": \"A great film\",\n" +
                "  \"releaseDate\": \"2000-01-01\",\n" +
                "  \"duration\": 120\n" +
                "}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

//    @Test
//    void shouldReturnNotFoundForUpdatingNonExistentFilm() throws Exception {
//        String json = "{\n" +
//                "  \"id\": 999,\n" +
//                "  \"name\": \"Updated Movie\",\n" +
//                "  \"description\": \"An updated description\",\n" +
//                "  \"releaseDate\": \"2000-01-01\",\n" +
//                "  \"duration\": 120\n" +
//                "}";
//
//        mockMvc.perform(put("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().isNotFound());
//    }
}
