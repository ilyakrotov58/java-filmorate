package ru.yandex.practicum.filmorate.models.filmAddModels;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
public class Mpa {

    public static Mpa G = new Mpa(1,"G");
    public static Mpa PG = new Mpa(2,"PG");
    public static Mpa PG13 = new Mpa(3,"PG-13");
    public static Mpa R = new Mpa(4,"R");
    public static Mpa NC17 = new Mpa(5,"NC-17");

    @Getter
    private int id;

    @Getter
    private String name;

    public static List<Mpa> getAll() {
        return Arrays.asList(G, PG, PG13, R, NC17);
    }

    public static Mpa getById(int id){
        Mpa result = null;
        switch (id){
            case 1: result = G;
            break;
            case 2: result = PG;
            break;
            case 3: result = PG13;
            break;
            case 4: result = R;
            break;
            case 5: result = NC17;
            break;
        }
        return result;
    }

    public static Mpa getByName(String code){
        Mpa result = null;
        switch (code){
            case "G": result = G;
                break;
            case "PG": result = PG;
                break;
            case "PG-13": result = PG13;
                break;
            case "R": result = R;
                break;
            case "NC-17": result = NC17;
                break;
        }
        return result;
    }
}
