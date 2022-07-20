package ru.yandex.practicum.filmorate.testUtils;

import ru.yandex.practicum.filmorate.models.User;
import java.time.LocalDate;

public class UserGenerator {

    public static User createUser(int id){
        return new User(
                id,
                "test@testEmail.com",
                "UserLogin_" + id,
                "UserName_" + id,
                LocalDate.of(2000, 1, 1),
                null);
    }
}
