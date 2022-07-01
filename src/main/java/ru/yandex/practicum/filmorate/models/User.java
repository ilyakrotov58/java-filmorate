package ru.yandex.practicum.filmorate.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {

    private int id;

    @Email(message = "Email should be format \"xxx@mm.com\"")
    @NotNull(message = "Email should not be empty")
    private String email;

    @NotBlank
    @NotNull
    private String login;

    @Nullable
    private String name;

    @Past
    private LocalDate birthday;
}
