package ru.yandex.practicum.filmorate.models.filmAddModels;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
public class Genre {

    @Getter
    private int id;

    @Getter
    private String name;
}
