package ru.yandex.practicum.filmorate.testUtils;

import ru.yandex.practicum.filmorate.models.Film;
import java.time.LocalDate;

public class FilmGenerator {

    public static Film createFilm(int id){
        return new Film(
                id,
                "filmName_" + id,
                "FilmDesc_" + id,
                LocalDate.of(2000, 1, 1),
                160);
    }
}
