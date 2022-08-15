package ru.yandex.practicum.filmorate.models.filmAddModels;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
public class Genre {

    public static Genre Comedy = new Genre(1,"Комедия");
    public static Genre Drama = new Genre(2,"Драма");
    public static Genre Cartoon = new Genre(3,"Мультфильм");
    public static Genre Thriller = new Genre(4,"Триллер");
    public static Genre Documentary = new Genre(5,"Документальное");
    public static Genre Action = new Genre(6,"Боевик");

    @Getter
    private int id;

    @Getter
    private String name;

    public static List<Genre> getAll() {
        return Arrays.asList(Comedy, Drama, Cartoon, Thriller, Documentary, Action);
    }

    public static Genre getById(int id){
        Genre result = null;
        switch (id){
            case 1: result = Comedy;
            break;
            case 2: result = Drama;
            break;
            case 3: result = Cartoon;
            break;
            case 4: result = Thriller;
            break;
            case 5: result = Documentary;
            break;
            case 6: result = Action;
            break;
        }
        return result;
    }

    public static Genre getByName(String code) {
        Genre result = null;
        switch (code){
            case "Комедия": result = Comedy;
                break;
            case "Драма": result = Drama;
                break;
            case "Мультик": result = Cartoon;
                break;
            case "Триллер": result = Thriller;
                break;
            case "Документальное": result = Documentary;
                break;
            case "Боевик": result = Action;
                break;
        }
        return result;
    }
}
