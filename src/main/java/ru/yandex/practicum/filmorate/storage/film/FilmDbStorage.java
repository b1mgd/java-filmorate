package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.RatingRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Set;

@Qualifier
@Repository
public class FilmDbStorage implements FilmStorage {

    private final FilmRepository repository;
    private final RatingRepository ratingRepository;
    private final GenreRepository genreRepository;

    public FilmDbStorage(FilmRepository repository,
                         RatingRepository ratingRepository,
                         GenreRepository genreRepository) {
        this.repository = repository;
        this.ratingRepository = ratingRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    public Collection<Film> getFilms() {
        return repository.findAll();
    }

    @Override
    public Film addFilm(Film film) {
        return repository.save(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return repository.update(film);
    }

    @Override
    public Film getFilmById(long filmId) {
        return repository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Film with id " + filmId + " not found"));
    }

    public Set<Long> getLikes(long filmId) {
        return repository.getLikes(filmId);
    }

    public void addLike(long filmId, long userId){
        repository.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId){
        repository.removeLike(filmId, userId);
    }

    public Collection<Genre> getGenres(){
        return genreRepository.findAll();
    }

    public Collection<Rating> getRating(){
        return ratingRepository.findAll();
    }

    public Genre getGenreById(int id){
        return genreRepository.findOne(id)
                .orElseThrow(() -> new NotFoundException("Genre with id " + id + " not found"));
    }

    public Rating getRatingById(int id){
        return ratingRepository.findOne(id)
                .orElseThrow(() -> new NotFoundException("Rating with id " + id + " not found"));
    }
}
