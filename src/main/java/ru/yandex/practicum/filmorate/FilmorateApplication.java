package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class FilmorateApplication {

    public static void main(final String[] args) {
        log.info("Сервер запущен");
        SpringApplication.run(FilmorateApplication.class, args);
    }
}
