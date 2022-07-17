package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.ArrayList;
import java.util.Optional;

public interface IFilmStorage {

    ArrayList<Film> getAll();

    Film add(Film film);

    Film update(Film film);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    ArrayList<Film> getMostPopularFilms(Optional<Integer> count);

    Film getFilmById(int filmId);
}
