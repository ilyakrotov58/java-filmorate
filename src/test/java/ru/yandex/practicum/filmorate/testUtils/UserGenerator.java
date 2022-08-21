package ru.yandex.practicum.filmorate.testUtils;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.models.User;
import java.time.LocalDate;
import java.util.HashMap;

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

    public static User createUserWithNextId(JdbcTemplate jdbcTemplate){
        String sqlQuery = "SELECT USER_ID FROM USERS ORDER BY USER_ID DESC LIMIT 1";
        var rs = jdbcTemplate.queryForRowSet(sqlQuery);
        int id = 0;

        if(rs.next()){
            id = rs.getInt("USER_ID") + 1;
        }

        return new User(
                id,
                DataGenerator.generateStringWithLength(3) + "testEmail.com",
                "UserLogin_" + DataGenerator.generateStringWithLength(3),
                "UserName_" + id,
                LocalDate.of(2000, 1, 1),
                new HashMap<>());
    }
}
