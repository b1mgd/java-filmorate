package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final FilmService filmService;

    public MpaController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public ResponseEntity<List<MpaDto>> getMpas() {
        return ResponseEntity.ok(filmService.getMpas());
    }

    @GetMapping("/{mpaId}")
    public ResponseEntity<MpaDto> getMpaById(@PathVariable Integer mpaId) {
        return ResponseEntity.ok(filmService.getMpaById(mpaId));
    }
}
