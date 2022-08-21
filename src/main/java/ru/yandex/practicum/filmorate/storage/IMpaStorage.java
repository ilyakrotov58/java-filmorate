package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.models.filmAddModels.Mpa;

import java.util.ArrayList;

public interface IMpaStorage {

    ArrayList<Mpa> getAllRatings();

    Mpa getRatingById(int id);
}
