package ru.yandex.practicum.filmorate.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.yandex.practicum.filmorate.models.filmAddModels.Genre;
import ru.yandex.practicum.filmorate.models.filmAddModels.Mpa;
import ru.yandex.practicum.filmorate.validators.filmMinDateValidator.FilmMinDateValidator;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Data
@AllArgsConstructor
public class Film {

    @JsonIgnore
    private static final String minFilmDate = "1895-12-28";

    private int id;

    @NotNull(message = "Film name should not be empty")
    @NotEmpty
    private String name;

    @Size(max = 200, message = "Max value of description is 200")
    private String description;

    @FilmMinDateValidator(message = "Date of film should be after " + minFilmDate)
    private LocalDate releaseDate;

    @Positive(message = "Should be > 0")
    private int duration;

    private LinkedHashSet<Genre> genres;

    @NotNull
    private Mpa mpa;

    @JsonIgnore
    @Getter
    @Setter
    private Set<Integer> userIdLikes;

    public static String getMinFilmDate() {
        return minFilmDate;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("FILM_ID", this.id);
        values.put("FILM_NAME", this.name);
        values.put("DESCRIPTION", this.description);
        values.put("RELEASE_DATE", this.releaseDate);
        values.put("FILM_DURATION", this.duration);
        values.put("RATING_ID", this.mpa.getId());

        return values;
    }
}
