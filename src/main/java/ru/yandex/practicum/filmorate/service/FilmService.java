package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;
    private final FilmRepository filmRepository;

    public FilmService(@Qualifier("filmRepository") FilmStorage filmStorage,
                       @Qualifier("userRepository") UserStorage userStorage,
                       MpaRepository mpaRepository,
                       GenreRepository genreRepository, FilmRepository filmRepository) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaRepository = mpaRepository;
        this.genreRepository = genreRepository;
        this.filmRepository = filmRepository;
    }

    public List<FilmDto> getAllFilms() {
        List<FilmDto> films = filmStorage.getAllFilms()
                .stream()
                .map(film -> {
                    Set<Integer> likes = filmStorage.getLikes(film.getId())
                            .stream()
                            .map(User::getId)
                            .collect(Collectors.toSet());
                    enrich(film);
                    return FilmMapper.maptoFilmDto(film, likes);
                })
                .collect(Collectors.toList());

        log.info("Получен список всех загруженных фильмов размером: {}", films.size());
        return films;
    }

    public FilmDto getFilmById(Integer filmId) {
        return filmStorage.getFilmById(filmId)
                .map(film -> {
                    Set<Integer> likes = filmStorage.getLikes(film.getId())
                            .stream()
                            .map(User::getId)
                            .collect(Collectors.toSet());
                    System.out.println("film = " + film);
                    enrich(film);
                    System.out.println("film = " + film);
                    return FilmMapper.maptoFilmDto(film, likes);
                })
                .orElseThrow(() -> {
                    log.warn("Фильм с filmId {} не был найден", filmId);
                    return new NotFoundException("Фильм с указанным Id не был найден");
                });
    }

    public List<FilmDto> getPopularFilms(int count) {
        List<Film> films = filmStorage.getAllFilms();

        if (films.isEmpty()) {
            log.info("Получен пустой список популярных фильмов");
            return Collections.emptyList();
        }

        return films.stream()
                .map(film -> {
                    Set<Integer> likes = filmStorage.getLikes(film.getId())
                            .stream()
                            .map(User::getId)
                            .collect(Collectors.toSet());
                    enrich(film);
                    return Map.entry(film, likes);
                })
                .sorted((e1, e2)
                        -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                .limit(count)
                .map(entry -> FilmMapper.maptoFilmDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public FilmDto createFilm(NewFilmRequest request) {
        validateRequest(request);

        Film film = FilmMapper.maptoFilm(request);
        checkMpaAndGenres(film);
        filmStorage.createFilm(film);
        saveGenresForFilm(film);
        enrich(film);

        log.info("В базу был добавлен фильм с id {}", film.getId());
        return FilmMapper.maptoFilmDto(film, Collections.emptySet());
    }

    public FilmDto updateFilm(UpdateFilmRequest request) {
        validateRequest(request);

        Film updatedFilm = filmStorage.getFilmById(request.getId())
                .map(film -> FilmMapper.updateFilmFields(film, request))
                .orElseThrow(() -> {
                    log.warn("Не удалось обновить информацию о фильме с filmId {}", request.getId());
                    return new NotFoundException("Фильм с filmId " + request.getId() + " не был найден");
                });

        checkMpaAndGenres(updatedFilm);
        updatedFilm = filmStorage.updateFilm(updatedFilm);
        saveGenresForFilm(updatedFilm);
        enrich(updatedFilm);

        return FilmMapper.maptoFilmDto(updatedFilm, Collections.emptySet());
    }

    public void addLike(Integer filmId, Integer userId) {
        validateFilmAndUser(filmId, userId);

        if (!filmStorage.addLike(filmId, userId)) {
            log.warn("Не удалось поставить лайк пользователя {} фильму {}", userId, filmId);
            throw new InternalServerException("Не удалось поставить лайк фильму");
        }

        log.info("Лайк пользователя {} добавлен фильму {}", userId, filmId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        validateFilmAndUser(filmId, userId);

        if (!filmStorage.removeLike(filmId, userId)) {
            log.warn("Не удалось убрать лайк пользователя {} с фильма {}", userId, filmId);
            throw new InternalServerException("Не удалось убрать лайк с фильма");
        }

        log.info("Лайк пользователя {} удален с фильма {}", userId, filmId);
    }

    public List<GenreDto> getGenres() {
        List<Genre> genres = genreRepository.getGenres();

        if (genres.isEmpty()) {
            log.info("Получен пустой список жанров");
            return Collections.emptyList();
        }

        log.info("Получен список жанров размером {}", genres.size());
        return genres.stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }

    public GenreDto getGenreById(Integer genreId) {
        Genre genre = genreRepository.getGenreById(genreId)
                .orElseThrow(() -> {
                    log.warn("Жанр с id {} не найден", genreId);
                    return new NotFoundException("Жанр с id " + genreId + " не найден");
                });

        log.info("По запросу получен жанр: {}", genre);
        return GenreMapper.mapToGenreDto(genre);
    }

    public List<MpaDto> getMpas() {
        List<Mpa> mpas = mpaRepository.getMpas();

        if (mpas.isEmpty()) {
            log.info("Получен пустой список рейтингов");
            return Collections.emptyList();
        }

        log.info("Получен список рейтингов размером {}", mpas.size());

        return mpas.stream()
                .map(MpaMapper::maptoMpaDto)
                .collect(Collectors.toList());
    }

    public MpaDto getMpaById(Integer mpaId) {
        Mpa mpa = mpaRepository.getMpaById(mpaId)
                .orElseThrow(() -> {
                    log.warn("Mpa с id {} не найден", mpaId);
                    return new NotFoundException("Рейтинг с id " + mpaId + " не найден");
                });

        log.info("По запросу получено возрастное ограничение: {}", mpa);
        return MpaMapper.maptoMpaDto(mpa);
    }

    private void validateFilmAndUser(Integer filmId, Integer userId) {
        if (filmStorage.getFilmById(filmId).isEmpty()) {
            throw new NotFoundException("Фильм с Id " + filmId + " не найден");
        }
        if (userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с Id " + userId + " не найден");
        }
    }

    private void validateRequest(NewFilmRequest request) {
        if (request.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE) ||
                request.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidateException("Неверные данные о дате выхода или описании фильма");
        }
    }

    private void validateRequest(UpdateFilmRequest request) {
        if (request.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE) ||
                request.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidateException("Неверные данные о дате выхода или описании фильма");
        }
    }

    private void checkMpaAndGenres(Film film) {
        checkMpa(film);
        checkGenre(film);
    }

    private void checkMpa(Film film) {
        if (film.getMpa() != null) {
            int mpaId = film.getMpa().getId();
            Set<Integer> validMpaIds = mpaRepository.getMpas()
                    .stream()
                    .map(Mpa::getId)
                    .collect(Collectors.toSet());

            if (!validMpaIds.contains(mpaId)) {
                throw new NotFoundException("MPA с id " + mpaId + " не найден");
            }
        }
    }

    private void checkGenre(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Integer> validGenreIds = genreRepository.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            boolean hasInvalidGenre = film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .anyMatch(id -> !validGenreIds.contains(id));

            if (hasInvalidGenre) {
                throw new NotFoundException("Фильм содержит недопустимые жанры");
            }
        }
    }

    private void enrich(Film film) {
        if (film.getMpa() != null) {
            int mpaId = film.getMpa().getId();
            Mpa fullMpa = mpaRepository.getMpaById(mpaId)
                    .orElseThrow(() -> new NotFoundException("MPA с id " + mpaId + " не найден"));
            film.setMpa(fullMpa);
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Integer> genreIds = film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            LinkedHashSet<Genre> enrichedGenres = genreRepository.getGenres()
                    .stream()
                    .filter(g -> genreIds.contains(g.getId()))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            film.setGenres(enrichedGenres);
        }
    }

    private void saveGenresForFilm(Film film) {
        filmRepository.deleteGenresByFilmId(film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                filmRepository.addGenreToFilm(film.getId(), genre.getId());
            }
        }
    }
}
