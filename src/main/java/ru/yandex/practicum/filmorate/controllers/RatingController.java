package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.models.filmAddModels.Mpa;
import ru.yandex.practicum.filmorate.services.FilmService;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping(value = "/mpa")
public class RatingController {

    @Autowired
    private FilmService filmService;

    @GetMapping
    public ArrayList<Mpa> getAllRatings() {
        return filmService.getAllRatings();
    }

    @GetMapping("/{id}")
    public Mpa getRatingById(@PathVariable int id) {
        return filmService.getRatingById(id);
    }
}
