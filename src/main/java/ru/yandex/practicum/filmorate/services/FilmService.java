package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.filmAddModels.Genre;
import ru.yandex.practicum.filmorate.models.filmAddModels.Mpa;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.IFilmStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.ArrayList;

@Service
public class FilmService {

    private final IFilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(IFilmStorage filmStorage, MpaStorage mpaStorage, GenreStorage genreStorage){
        this.filmStorage = filmStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public ArrayList<Film> getAll(){
        return filmStorage.getAll();
    }

    public Film add(Film film){
        return filmStorage.add(film);
    }

    public Film update(Film film){
        return filmStorage.update(film);
    }

    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmId, userId);
    }

    public ArrayList<Genre> getAllGenres(){
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(int id){
        return genreStorage.getGenreById(id);
    }

    public ArrayList<Mpa> getAllRatings() {
        return mpaStorage.getAllRatings();
    }

    public Mpa getRatingById(int id) {
        return mpaStorage.getRatingById(id);
    }

    public void deleteLike(int filmId, int userId){
        filmStorage.deleteLike(filmId, userId);
    }

    public ArrayList<Film> getMostPopularFilms(int count) {
        return filmStorage.getMostPopularFilms(count);
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }
}
