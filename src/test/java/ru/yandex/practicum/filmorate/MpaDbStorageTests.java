package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.models.filmAddModels.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.ArrayList;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorageTests {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;

    @Test
    public void getAllRatings() {
        // Arrange
        var expectedRatings = new ArrayList<Mpa>();
        String sqlQuery = "SELECT * FROM FILM_RATINGS ORDER BY ID";
        var rs = jdbcTemplate.queryForRowSet(sqlQuery);

        while (rs.next()) {
            var mpa = new Mpa(
                    rs.getInt("ID"),
                    rs.getString("RATING"));
            expectedRatings.add(mpa);
        }

        // Act
        var actualRatings = mpaStorage.getAllRatings();

        // Assert
        Assertions.assertEquals(expectedRatings, actualRatings);
    }

    @Test
    public void getRatingById() {
        // Arrange
        String sqlQuery = "SELECT * FROM FILM_RATINGS WHERE ID = ?";
        var rs = jdbcTemplate.queryForRowSet(sqlQuery, 1);
        Mpa expectedMpa = null;

        while (rs.next()) {
            expectedMpa = new Mpa(
                    rs.getInt("ID"),
                    rs.getString("RATING"));
        }

        // Act
        var actualRating = mpaStorage.getRatingById(1);

        // Assert
        Assertions.assertEquals(expectedMpa, actualRating);
    }
}
