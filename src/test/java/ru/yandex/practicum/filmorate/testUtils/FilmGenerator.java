package ru.yandex.practicum.filmorate.testUtils;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.filmAddModels.Genre;
import ru.yandex.practicum.filmorate.models.filmAddModels.Mpa;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class FilmGenerator {

    public static Film createFilm(int id){

        var listMpa = new LinkedHashSet<Genre>();
        listMpa.add(new Genre(6, "Боевик"));

        return new Film(
                id,
                "filmName_" + id,
                "FilmDesc_" + id,
                LocalDate.of(2000, 1, 1),
                160,
                listMpa,
                new Mpa(1, "G"),
                new HashSet<>());
    }

    public static Film createFilmWithNextId(JdbcTemplate jdbcTemplate){

        var listMpa = new LinkedHashSet<Genre>();
        listMpa.add(new Genre(6, "Боевик"));

        String sqlQuery = "SELECT FILM_ID FROM FILMS ORDER BY FILM_ID DESC LIMIT 1";
        var rs = jdbcTemplate.queryForRowSet(sqlQuery);
        int id = 0;

        if(rs.next()){
            id = rs.getInt("FILM_ID") + 1;
        }

        return new Film(
                id,
                DataGenerator.generateStringWithLength(7),
                "FilmDesc_" + id,
                LocalDate.of(2000, 1, 1),
                160,
                listMpa,
                new Mpa(1, "G"),
                new HashSet<>());
    }
}
