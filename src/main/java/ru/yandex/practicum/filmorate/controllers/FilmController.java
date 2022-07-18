package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.services.UserService;

import javax.validation.Valid;
import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping(value = "/films")
public class FilmController {

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ArrayList<Film> getAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        validateIfFilmExist(id, true);
        return filmService.getFilmById(id);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        validateName(film);
        validateIfFilmExist(film.getId(), false);
        log.info("Film " + film.getName() + " with id=" + film.getId() + " was added");
        return filmService.add(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validateIfFilmExist(film.getId(), true);
        log.info("Film " + film.getName() + " with id=" + film.getId() + " was updated");
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        validateIfFilmExist(id, true);
        validateIfUserExist(userId);
        log.info("User with id=" + id + " set like to user with id=" + userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        validateIfFilmExist(id, true);
        validateIfUserExist(userId);
        log.info("User with id=" + id + " delete like to user with id=" + userId);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public ArrayList<Film> getMostPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getMostPopularFilms(count);
    }

    private void validateIfUserExist(int userId) {
        if (userService.getUser(userId) == null) {
            throw new NotFoundException("User with id=" + userId + " is not exist");
        }
    }

    private void validateIfFilmExist(int filmId, boolean existShouldBeTrue) {
        if (existShouldBeTrue) {
            if (filmService.getFilmById(filmId) == null) {
                throw new NotFoundException("Film with id=" + filmId + " was not found");
            }
        } else if (filmId != 0 && filmService.getFilmById(filmId) != null) {
            throw new AlreadyExistException("Film with id=" + filmId + " already exist");
        }
    }

    private void validateName(Film film) throws AlreadyExistException {
        for (Film existingFilm : filmService.getAll()) {
            if (film.getName().equalsIgnoreCase(existingFilm.getName())) {
                throw new AlreadyExistException("Film with name " + film.getName() + " already exist");
            }
        }
    }
}
