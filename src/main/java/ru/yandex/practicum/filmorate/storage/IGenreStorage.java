package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.models.filmAddModels.Genre;

import java.util.ArrayList;

public interface IGenreStorage {

    ArrayList<Genre> getAllGenres();

    Genre getGenreById(int id);

}
