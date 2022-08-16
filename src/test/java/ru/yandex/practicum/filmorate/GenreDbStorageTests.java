package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.models.filmAddModels.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.ArrayList;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTests {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    @Test
    public void getAllGenres() {
        // Arrange
        ArrayList<Genre> expectedGenres = new ArrayList<>();
        String sqlQuery = "SELECT * FROM GENRES ORDER BY ID";
        var rs = jdbcTemplate.queryForRowSet(sqlQuery);

        while (rs.next()) {
            var genre = new Genre(
                    rs.getInt("ID"),
                    rs.getString("GENRE"));
            expectedGenres.add(genre);
        }

        // Act
        var actualGenres = genreStorage.getAllGenres();

        // Assert
        Assertions.assertEquals(expectedGenres, actualGenres);
    }

    @Test
    public void getGenreById() {
        // Arrange
        String sqlQuery = "SELECT * FROM GENRES WHERE ID = ?";
        var rs = jdbcTemplate.queryForRowSet(sqlQuery, 1);
        Genre expectedGenre = null;

        while (rs.next()) {
            expectedGenre = new Genre(
                    rs.getInt("ID"),
                    rs.getString("GENRE"));
        }

        // Act
        var actualGenre = genreStorage.getGenreById(1);

        // Assert
        Assertions.assertEquals(expectedGenre, actualGenre);
    }
}
