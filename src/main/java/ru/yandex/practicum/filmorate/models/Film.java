package ru.yandex.practicum.filmorate.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validators.filmMinDateValidator.FilmMinDateValidator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {

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

    public static String getMinFilmDate() {
        return minFilmDate;
    }
}
