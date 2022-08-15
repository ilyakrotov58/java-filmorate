package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.filmAddModels.Genre;
import ru.yandex.practicum.filmorate.models.filmAddModels.Mpa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements IFilmStorage {

    @Getter
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ArrayList<Film> getAll() {
        var films = new ArrayList<Film>();
        String allFilmsQuery = "SELECT * FROM FILMS";
        var rs = jdbcTemplate.queryForRowSet(allFilmsQuery);
        while (rs.next()) {
            films.add(makeFilm(rs));
        }

        return films;
    }

    @Override
    public Film add(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        var returnedFilmId = (int) simpleJdbcInsert.executeAndReturnKey(film.toMap());
        film.setId(returnedFilmId);

        if(film.getGenres() != null && film.getGenres().size() > 0){
            String sqlQuery = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
            for (Genre filmGenre : film.getGenres()) {
                jdbcTemplate.update(sqlQuery, film.getId(), filmGenre.getId());
            }
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, " +
                "FILM_DURATION = ?, RATING_ID = ? WHERE FILM_ID = ?";

        jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        String query = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(query, film.getId());

        if(film.getGenres() != null){
            for (Genre genre : film.getGenres()) {
                String updateQuery = "INSERT INTO FILM_GENRE VALUES (?, ?)";
                jdbcTemplate.update(updateQuery, film.getId(), genre.getId());
            }
        }

        String returnedFilmQuery = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        var rs = jdbcTemplate.queryForRowSet(returnedFilmQuery, film.getId());

        Film returnedFilm = null;
        if (rs.next()) {
            returnedFilm = makeFilm(rs);
        }
        return returnedFilm;
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO FILM_LIKES VALUES(?, ?)";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE USER_ID = ? AND FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public ArrayList<Film> getMostPopularFilms(int count) {
        var films = new ArrayList<Film>();

        String sqlQuery =
                "SELECT fl.FILM_ID, " +
                        "FILM_NAME, " +
                        "DESCRIPTION, " +
                        "RELEASE_DATE,  " +
                        "FILM_DURATION, " +
                        "RATING_ID, " +
                        "COUNT(fl.USER_ID) as cnt " +
                        "FROM FILM_LIKES as fl" +
                        " INNER JOIN FILMS as f ON f.FILM_ID = fl.FILM_ID" +
                        " GROUP BY fl.FILM_ID" +
                        " ORDER BY cnt DESC LIMIT ?";

        var rs = jdbcTemplate.queryForRowSet(sqlQuery, count);

        while (rs.next()) {
            films.add(makeFilm(rs));
        }

        var otherFilms = new ArrayList<Film>();
        if(films.size() < count){
            String query = "SELECT * FROM FILMS LIMIT ?";
            var rsForAll = jdbcTemplate.queryForRowSet(query, count - films.size());

            while (rsForAll.next()){
                otherFilms.add(makeFilm(rsForAll));
            }

            films.addAll(otherFilms);
        }

        return films;
    }

    @Override
    public Film getFilmById(int filmId) {
        String sqlQuery = "SELECT * FROM FILMS WHERE FILM_ID = ?";

        var filmRs = jdbcTemplate.queryForRowSet(sqlQuery, filmId);

        Film film = null;

        if (filmRs.first()) {
            film = makeFilm(filmRs);
        }

        return film;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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
                    Mpa.getById(rs.getInt("RATING_ID")),
                    getUserIdLikes(filmId));
    }

    private LinkedHashSet<Genre> getFilmGenres(int filmId) {

        var genres = new LinkedHashSet<Genre>();
        String genresQuery = "SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?";
        var rsGenres = jdbcTemplate.queryForRowSet(genresQuery, filmId);

        while (rsGenres.next()) {
            genres.add(Genre.getById(rsGenres.getInt("GENRE_ID")));
        }

        return genres;
    }

    private Set<Integer> getUserIdLikes(int filmId) {
        var userIds = new HashSet<Integer>() {};

        String likesQuery = "SELECT USER_ID FROM FILM_LIKES WHERE FILM_ID = ?";
        var rs = jdbcTemplate.queryForRowSet(likesQuery, filmId);

        while (rs.next()) {
            userIds.add(rs.getInt("USER_ID"));
        }

        return userIds;
    }
}
