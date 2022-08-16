package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.filmAddModels.Genre;
import ru.yandex.practicum.filmorate.models.filmAddModels.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.testUtils.FilmGenerator;
import ru.yandex.practicum.filmorate.testUtils.UserGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTests {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindFilmById() {

        // Arrange
        var expectedFilm = FilmGenerator
                .createFilmWithNextId(filmDbStorage.getJdbcTemplate());

        filmDbStorage.add(expectedFilm);

        // Act
        var actualFilm = filmDbStorage.getFilmById(expectedFilm.getId());

        // Assert
        Assertions.assertEquals(expectedFilm, actualFilm);
    }

    @Test
    public void testGetAllFilms() {
        // Arrange
        var expectedList = new ArrayList<Film>();
        var film = FilmGenerator.createFilmWithNextId(filmDbStorage.getJdbcTemplate());
        filmDbStorage.add(film);

        String query = "SELECT * FROM FILMS " +
                "INNER JOIN FILM_RATINGS FR on FILMS.RATING_ID = FR.ID";
        var rs = jdbcTemplate.queryForRowSet(query);
        while (rs.next()) {
            expectedList.add(makeFilm(rs));
        }

        // Act
        var actualList = filmDbStorage.getAll();

        // Assert
        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    public void testUpdateFilm() {
        // Arrange
        var expectedFilm = FilmGenerator
                .createFilmWithNextId(filmDbStorage.getJdbcTemplate());
        filmDbStorage.add(expectedFilm);

        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(new Genre(2, "Драма"));
        genres.add(new Genre(3, "Мультфильм"));

        expectedFilm.setName("newName");
        expectedFilm.setDescription("newDesc");
        expectedFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        expectedFilm.setDuration(100);
        expectedFilm.setGenres(genres);
        expectedFilm.setMpa(new Mpa(4, "R"));

        // Act
        var actualFilm = filmDbStorage.update(expectedFilm);

        // Assert
        Assertions.assertEquals(expectedFilm, actualFilm);
    }

    @Test
    public void testAddLike() {
        // Arrange
        var film = FilmGenerator.createFilmWithNextId(filmDbStorage.getJdbcTemplate());
        var user = UserGenerator.createUserWithNextId(userDbStorage.getJdbcTemplate());

        filmDbStorage.add(film);
        userDbStorage.add(user);

        // Act
        filmDbStorage.addLike(film.getId(), user.getId());

        // Assert
        String query = "SELECT * FROM FILM_LIKES WHERE FILM_ID = ?";
        var rs = jdbcTemplate.queryForRowSet(query, film.getId());

        while (rs.next()) {
            Assertions.assertEquals(film.getId(), rs.getInt("FILM_ID"));
            Assertions.assertEquals(user.getId(), rs.getInt("USER_ID"));
        }
    }

    @Test
    public void testDeleteLike() {
        // Arrange
        var film = FilmGenerator.createFilmWithNextId(filmDbStorage.getJdbcTemplate());
        var user = UserGenerator.createUserWithNextId(userDbStorage.getJdbcTemplate());

        filmDbStorage.add(film);
        userDbStorage.add(user);

        String insertQuery = "INSERT INTO FILM_LIKES VALUES (?, ?)";
        jdbcTemplate.update(insertQuery, user.getId(), film.getId());

        // Act
        filmDbStorage.deleteLike(film.getId(), user.getId());

        // Assert
        String query = "SELECT * FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        var rs = jdbcTemplate.queryForRowSet(query, film.getId(), user.getId());

        Assertions.assertFalse(rs.next());
    }

    @Test
    public void testGetMostPopularFilms() {
        // Arrange
        var expectedFilm = FilmGenerator.createFilmWithNextId(filmDbStorage.getJdbcTemplate());
        var film2 = FilmGenerator.createFilmWithNextId(filmDbStorage.getJdbcTemplate());
        var user1 = UserGenerator.createUserWithNextId(userDbStorage.getJdbcTemplate());
        var user2 = UserGenerator.createUserWithNextId(userDbStorage.getJdbcTemplate());
        var user3 = UserGenerator.createUserWithNextId(userDbStorage.getJdbcTemplate());

        filmDbStorage.add(expectedFilm);
        filmDbStorage.add(film2);
        userDbStorage.add(user1);
        userDbStorage.add(user2);
        userDbStorage.add(user3);

        String insertQuery = "INSERT INTO FILM_LIKES VALUES (?, ?)";
        jdbcTemplate.update(insertQuery, user1.getId(), expectedFilm.getId());
        jdbcTemplate.update(insertQuery, user2.getId(), expectedFilm.getId());
        jdbcTemplate.update(insertQuery, user3.getId(), film2.getId());

        expectedFilm.getUserIdLikes().add(user1.getId());
        expectedFilm.getUserIdLikes().add(user2.getId());

        // Act
        var actualFilms = filmDbStorage.getMostPopularFilms(1);

        // Assert
        Assertions.assertEquals(expectedFilm, actualFilms.get(0));
    }

    private Film makeFilm(SqlRowSet rs) {
        var releaseDate = rs.getDate("RELEASE_DATE");
        var filmId = rs.getInt("FILM_ID");

        return new Film(
                filmId,
                rs.getString("FILM_NAME"),
                rs.getString("DESCRIPTION"),
                releaseDate != null ? releaseDate.toLocalDate() : null,
                rs.getInt("FILM_DURATION"),
                getFilmGenres(filmId),
                new Mpa(rs.getInt("RATING_ID"),
                        rs.getString("RATING")),
                getUserIdLikes(filmId));
    }

    private LinkedHashSet<Genre> getFilmGenres(int filmId) {

        var genres = new LinkedHashSet<Genre>();
        String genresQuery = "SELECT GENRE_ID, GENRE FROM FILM_GENRE AS FG " +
                "INNER JOIN GENRES AS G ON FG.GENRE_ID = G.ID " +
                "WHERE FILM_ID = ?";
        var rsGenres = jdbcTemplate.queryForRowSet(genresQuery, filmId);

        while (rsGenres.next()) {
            genres.add(new Genre(
                    rsGenres.getInt("GENRE_ID"),
                    rsGenres.getString("GENRE")));
        }

        return genres;
    }

    private Set<Integer> getUserIdLikes(int filmId) {
        var userIds = new HashSet<Integer>() {
        };

        String likesQuery = "SELECT USER_ID FROM FILM_LIKES WHERE FILM_ID = ?";
        var rs = jdbcTemplate.queryForRowSet(likesQuery, filmId);

        while (rs.next()) {
            userIds.add(rs.getInt("USER_ID"));
        }

        return userIds;
    }
}
