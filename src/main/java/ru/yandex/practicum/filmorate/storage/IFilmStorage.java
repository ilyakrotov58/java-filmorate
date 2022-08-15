package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.filmAddModels.Genre;
import ru.yandex.practicum.filmorate.models.filmAddModels.Mpa;

import java.util.ArrayList;

public interface IFilmStorage {

    ArrayList<Film> getAll();

    Film add(Film film);

    Film update(Film film);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    ArrayList<Film> getMostPopularFilms(int count);

    Film getFilmById(int filmId);

    ArrayList<Genre> getAllGenres();

    Genre getGenreById(int id);

    ArrayList<Mpa> getAllRatings();

    Mpa getRatingById(int id);
}
