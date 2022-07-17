package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Getter
public class InMemoryFilmStorage implements IFilmStorage {

    private static final HashMap<Integer, Film> films = new HashMap<>();
    private static int nextId = 0;

    @Override
    public ArrayList<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film add(Film film) {
        film.setId(setNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.replace(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(int filmId) {
        return films.get(filmId);
    }

    @Override
    public void addLike(int filmId, int userId) {
        var film = films.get(filmId);
        film.getUserIdLikes().add(userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        var film = films.get(filmId);
        film.getUserIdLikes().remove(userId);
    }

    @Override
    public ArrayList<Film> getMostPopularFilms(Optional<Integer> count) {
        var valueOrDefault = count.orElse(10);
        return films.values().stream()
                .sorted(this::compare)
                .limit(valueOrDefault)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private int compare(Film f0, Film f1) {
        int result = 0;
        if (f0.getUserIdLikes().size() > f1.getUserIdLikes().size()) {
            result = -1;
        }
        return result;
    }

    private int setNextId() {
        return ++nextId;
    }

    // Temporary methods. Will be deleted after we will have real db in project

    public static void setStartId0() {
        nextId = 0;
    }

    public static void clearDb() {
        films.clear();
    }
}
