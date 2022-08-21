package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.filmAddModels.Mpa;

import java.util.ArrayList;

@Component
public class MpaStorage implements IMpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ArrayList<Mpa> getAllRatings() {
        var ratings = new ArrayList<Mpa>();
        String sqlQuery = "SELECT * FROM FILM_RATINGS ORDER BY ID";
        var rs = jdbcTemplate.queryForRowSet(sqlQuery);

        while (rs.next()) {
            var rating = new Mpa(
                    rs.getInt("ID"),
                    rs.getString("RATING"));
            ratings.add(rating);
        }

        return ratings;
    }

    public Mpa getRatingById(int id) {
        Mpa mpa;

        String sqlQuery = "SELECT * FROM FILM_RATINGS WHERE ID = ?";
        var rs = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (rs.next()) {
            mpa = new Mpa(
                    rs.getInt("ID"),
                    rs.getString("RATING"));
        } else {
            throw new NotFoundException("Mpa with id =" + id + "can't be found");
        }

        return mpa;
    }
}
