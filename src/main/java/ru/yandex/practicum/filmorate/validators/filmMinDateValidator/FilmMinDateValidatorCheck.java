package ru.yandex.practicum.filmorate.validators.filmMinDateValidator;

import ru.yandex.practicum.filmorate.models.Film;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FilmMinDateValidatorCheck implements ConstraintValidator<FilmMinDateValidator, LocalDate> {

    private static final LocalDate minFileDate = LocalDate.parse(Film.getMinFilmDate());

    @Override
    public void initialize(FilmMinDateValidator dateTimeValidator) {
    }

    @Override
    public boolean isValid(LocalDate dateOfFilm, ConstraintValidatorContext constraintValidatorContext) {
        return dateOfFilm.isAfter(minFileDate);
    }
}
