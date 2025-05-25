package filmorate.service.film;

import filmorate.model.*;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import filmorate.dto.FilmDto;
import filmorate.dto.GenreDto;
import filmorate.dto.RatingDto;
import filmorate.exception.InternalServerException;
import filmorate.exception.NotFoundException;
import filmorate.exception.ParameterNotValidException;
import filmorate.mappers.FilmMapper;
import filmorate.mappers.GenreMapper;
import filmorate.mappers.RatingMapper;
import filmorate.service.feed.FeedService;
import filmorate.storage.film.FilmDbStorage;
import filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final FeedService feedService;

    public Collection<FilmDto> getFilms() {
        log.info("Getting all films");
        Collection<Film> films = filmStorage.getFilms();
        return films.stream()
                .map(film -> FilmMapper.mapToFilmDto(film, filmStorage.getLikes(film.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public FilmDto addFilm(FilmDto filmDto) {
        log.info("Adding new film: {}", filmDto);
        Film film = FilmMapper.mapToFilmDto(filmDto);
        validateFilm(film);
        Film addedFilm = filmStorage.addFilm(film);
        log.info("Film added with id: {}", addedFilm.getId());
        return FilmMapper.mapToFilmDto(addedFilm, Set.of());
    }

    @Transactional
    public FilmDto updateFilm(Film film) {
        log.info("Updating film with id: {}", film.getId());
        if (film.getId() <= 0) {
            throw new NotFoundException("Film ID must be positive for update.");
        }
        validateFilm(film);
        Film updatedFilm = filmStorage.updateFilm(film);
        log.info("Film updated: {}", updatedFilm);
        return FilmMapper.mapToFilmDto(updatedFilm, filmStorage.getLikes(updatedFilm.getId()));
    }

    public FilmDto getFilmById(long filmId) {
        log.info("Getting film by id: {}", filmId);
        Film film = filmStorage.getFilmById(filmId);
        return FilmMapper.mapToFilmDto(film, filmStorage.getLikes(filmId));
    }

    @Transactional
    public void addLikeToFilm(long filmId, long userId) {
        log.info("Adding like to film {} from user {}", filmId, userId);
        filmExists(filmId);
        userExists(userId);
        filmStorage.addLike(filmId, userId);
        log.info("Like added successfully");
        feedService.logEvent(userId, EventType.LIKE, Operation.ADD, filmId);
    }

    @Transactional
    public void deleteLikeToFilm(long filmId, long userId) {
        log.info("Deleting like from film {} by user {}", filmId, userId);
        filmExists(filmId);
        userExists(userId);
        filmStorage.removeLike(filmId, userId);
        log.info("Like removed successfully");
        feedService.logEvent(userId, EventType.LIKE, Operation.REMOVE, filmId);
    }

    @Transactional
    public List<FilmDto> getTopFilms(int count, int genreId, int year) {
        log.info("Getting top {} popular films with genreId = {} and release year = {}", count, genreId, year);
        if (count <= 0) {
            throw new ValidationException("Count parameter must be positive.");
        }
        return filmStorage.getTopFilms(count, genreId, year)
                .stream()
                .map(film -> {
                    Set<Long> likes = filmStorage.getLikes(film.getId());
                    return FilmMapper.mapToFilmDto(film, likes);
                })
                .toList();
    }

    public Collection<GenreDto> getGenres() {
        log.info("Getting all genres");
        return filmStorage.getGenres().stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }

    public GenreDto getGenreById(int id) {
        log.info("Getting genre by id: {}", id);
        Genre genre = filmStorage.getGenreById(id);
        return GenreMapper.mapToGenreDto(genre);
    }

    public Collection<RatingDto> getRatings() {
        log.info("Getting all MPA ratings");
        return filmStorage.getRatings().stream()
                .map(RatingMapper::mapToRatingDto)
                .collect(Collectors.toList());
    }

    public RatingDto getRatingById(int id) {
        log.info("Getting MPA rating by id: {}", id);
        Rating rating = filmStorage.getRatingById(id);
        return RatingMapper.mapToRatingDto(rating);
    }


    public List<FilmDto> getCommonFilms(long userId, long friendId) {
        userExists(userId);
        userExists(friendId);
        if (userId == friendId) {
            throw new ParameterNotValidException("Data", "Id's can't be the same");
        }
        log.info("Getting MPA common films for users: {}, {}", userId, friendId);
        Collection<Film> films = filmStorage.getCommonFilms(userId, friendId);
        return films.stream()
                .map(film -> FilmMapper.mapToFilmDto(film, filmStorage.getLikes(film.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteFilm(long filmId) {
        FilmDto filmDto = getFilmById(filmId);
        log.info("Deleting film: {}", filmDto);

        boolean isDeleted = filmStorage.deleteFilm(filmId);

        if (isDeleted) {
            log.info("Film deleted successfully");
        } else {
            throw new InternalServerException("Film was not deleted due to internal error.");
        }

    }

    public List<FilmDto> getRecommendations(long id) {
        userExists(id);
        log.info("Getting recommendations films for user with id = {}", id);
        Collection<Film> films = filmStorage.getRecommendations(id);
        return films.stream()
                .map(film -> FilmMapper.mapToFilmDto(film, filmStorage.getLikes(film.getId())))
                .collect(Collectors.toList());
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Validation failed");
            throw new ParameterNotValidException("Data", "Film release date cannot be earlier than " + MIN_RELEASE_DATE);
        }
    }

    private void userExists(long userId) {
        userStorage.getUserById(userId);
    }

    private void filmExists(long filmId) {
        filmStorage.getFilmById(filmId);
    }

    public List<FilmDto> findByDirector(long directorId, String sortMode) {
        log.info("Getting films by director with id: {} and sort mode {}", directorId, sortMode);
        Collection<Film> films = filmStorage.findByDirector(directorId, sortMode);
        if (films.isEmpty()) {
            log.error("No data found matching the specified parameters (id: {} and sort mode {})",
                    directorId, sortMode);
            throw new NotFoundException("No data found matching the specified parameters");
        }
        return films.stream()
                .map(film -> FilmMapper.mapToFilmDto(film, filmStorage.getLikes(film.getId())))
                .collect(Collectors.toList());
    }

    public List<FilmDto> searchFilms(String searchText, String searchBy) {
        Collection<Film> films = filmStorage.searchFilms(searchText, searchBy);
        log.info("Getting films by {} and search text {}", searchBy, searchText);
        return films.stream()
                .map(film -> FilmMapper.mapToFilmDto(film, filmStorage.getLikes(film.getId())))
                .collect(Collectors.toList());
    }

}