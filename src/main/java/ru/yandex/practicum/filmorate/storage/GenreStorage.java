package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.filmAddModels.Genre;

import java.util.ArrayList;

@Component
public class GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ArrayList<Genre> getAllGenres() {
        var genres = new ArrayList<Genre>();
        String sqlQuery = "SELECT * FROM GENRES ORDER BY ID";
        var rs = jdbcTemplate.queryForRowSet(sqlQuery);

        while (rs.next()) {
            var genre = new Genre(
                    rs.getInt("ID"),
                    rs.getString("GENRE"));
            genres.add(genre);
        }

        return genres;
    }

    public Genre getGenreById(int id) {
        Genre genre;

        String sqlQuery = "SELECT * FROM GENRES WHERE ID = ?";
        var rs = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (rs.next()) {
            genre = new Genre(
                    rs.getInt("ID"),
                    rs.getString("GENRE"));
        } else {
            throw new NotFoundException("Genre with id =" + id + "can't be found");
        }

        return genre;
    }
}
