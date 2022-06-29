package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping(value = "/films")
public class FilmController {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private int nextId = 0;

    @GetMapping
    public ArrayList<Film> getAll(){
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film, HttpServletResponse response) throws IOException {
        try {
            if (!films.containsKey(film.getId())) {
                validateName(film, response);
                film.setId(setNextId());
                films.put(film.getId(), film);
            } else {
                throw new AlreadyExistException("Film " + film.getName() + " is already exist");
            }
        } catch (AlreadyExistException ex){
            log.error(ex.getMessage());
            response.sendError(400);
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film, HttpServletResponse response) throws IOException {
        try{
            if(films.containsKey(film.getId())){
                films.replace(film.getId(), film);
            } else {
                throw new NotFoundException("Film " + film.getName() + " is not exist");
            }
        } catch (NotFoundException ex){
            log.error(ex.getMessage());
            response.sendError(500);
        } catch (ValidationException | javax.validation.ValidationException ex){
            log.error(ex.getMessage());
            response.sendError(400);
        }
        return film;
    }

    private int setNextId() {
        return ++nextId;
    }

    private void validateName(Film film, HttpServletResponse response) throws IOException{
        try {
            for (Film existingFilm : films.values()) {
                if (film.getName().equalsIgnoreCase(existingFilm.getName())) {
                    throw new AlreadyExistException("Film with name " + film.getName() + " already exist");
                }
            }
        } catch (AlreadyExistException ex){
            response.sendError(400);
            log.error(ex.getMessage());
        }
    }
}
