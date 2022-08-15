package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.testUtils.UserGenerator;

import javax.validation.valueextraction.Unwrapping;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTests {

    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;


    @Test
    public void testGetAllUsers() throws SQLException {
        // Arrange
        var expectedList = new ArrayList<User>();
        var user = UserGenerator.createUserWithNextId(userDbStorage.getJdbcTemplate());
        userDbStorage.add(user);

        String query = "SELECT * FROM USERS";
        var rs = jdbcTemplate.queryForRowSet(query);
        while (rs.next()){
            expectedList.add(makeUser(rs));
        }

        // Act
        var actualList = userDbStorage.getAll();

        // Assert
        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    public void getUserByIdTest() {
        // Arrange
        var expectedUser = UserGenerator.createUserWithNextId(jdbcTemplate);
        userDbStorage.add(expectedUser);

        // Act
        var actualUser = userDbStorage.getUserById(expectedUser.getId());

        // Assert
        Assertions.assertEquals(expectedUser, actualUser);
    }

    @Test
    public void deleteUserTest() {
        // Arrange
        var user = UserGenerator.createUserWithNextId(jdbcTemplate);
        userDbStorage.add(user);

        // Act
        userDbStorage.deleteUser(user);

        // Assert
        String query = "SELECT * FROM USERS WHERE USER_ID = ?";
        var rs = jdbcTemplate.queryForRowSet(query, user.getId());
        Assertions.assertFalse(rs.next());
    }

    @Test
    public void testUpdateUser() {
        // Arrange
        var expectedUser = UserGenerator
                .createUserWithNextId(userDbStorage.getJdbcTemplate());
        userDbStorage.add(expectedUser);

        expectedUser.setName("newName");
        expectedUser.setLogin("newLogin");
        expectedUser.setEmail("new@mail.ru");
        expectedUser.setBirthday(LocalDate.of(2000, 1, 1));

        // Act
        var actualUser = userDbStorage.update(expectedUser);

        // Assert
        Assertions.assertEquals(expectedUser, actualUser);
    }

    @Test
    public void testAddUserFriend() {
        // Arrange
        var user = UserGenerator.createUserWithNextId(userDbStorage.getJdbcTemplate());
        userDbStorage.add(user);

        var friend = UserGenerator.createUserWithNextId(userDbStorage.getJdbcTemplate());
        userDbStorage.add(friend);

        // Act
        userDbStorage.addFriend(user.getId(), friend.getId());

        // Assert
        String sqlQuery = "SELECT * FROM FRIENDSHIPS WHERE USER_ID = ?";
        var rs = jdbcTemplate.queryForRowSet(sqlQuery, user.getId());

        while (rs.next()){
            Assertions.assertEquals(
                    user.getId(),
                    rs.getInt("USER_ID"));

            Assertions.assertEquals(
                    friend.getId(),
                    rs.getInt("FRIEND_ID"));

            Assertions.assertFalse(rs.getBoolean("CONFIRMATION"));
        }
    }

    @Test
    public void testDeleteUserFriend() {
        // Arrange
        var friend = UserGenerator.createUserWithNextId(userDbStorage.getJdbcTemplate());
        userDbStorage.add(friend);

        var user = UserGenerator.createUserWithNextId(userDbStorage.getJdbcTemplate());

        var friendMap = new HashMap<Integer, Boolean>();
        friendMap.put(friend.getId(), true);

        user.setFriends(friendMap);

        userDbStorage.add(user);

        // Act
        userDbStorage.deleteFriend(user.getId(), friend.getId());

        // Assert
        String sqlQuery = "SELECT * FROM FRIENDSHIPS WHERE USER_ID = ? AND FRIEND_ID = ?";
        var rs = jdbcTemplate.queryForRowSet(sqlQuery, user.getId(), friend.getId());

        Assertions.assertFalse(rs.next());
    }

    @Test
    public void getUserFriends(){
        // Arrange
        var expectedFriends = new ArrayList<User>();
        var friend = UserGenerator.createUserWithNextId(userDbStorage.getJdbcTemplate());
        userDbStorage.add(friend);
        expectedFriends.add(friend);

        var user = UserGenerator.createUserWithNextId(userDbStorage.getJdbcTemplate());

        var friendMap = new HashMap<Integer, Boolean>();
        friendMap.put(friend.getId(), true);
        user.setFriends(friendMap);
        userDbStorage.add(user);

        // Act
        var actualFriends = userDbStorage.getFriends(user.getId());

        // Assert
        Assertions.assertEquals(expectedFriends, actualFriends);
    }

//    @Test
//    public void getCommonFriends(){
//        // Arrange
//        var commonFriend = UserGenerator.createUserWithNextId(jdbcTemplate);
//        userDbStorage.add(commonFriend);
//        var expectedFriends = new ArrayList<User>();
//        expectedFriends.add(commonFriend);
//
//        var friendMap = new HashMap<Integer, Boolean>();
//        friendMap.put(commonFriend.getId(), false);
//
//        var user1 = UserGenerator.createUserWithNextId(jdbcTemplate);
//        user1.setFriends(friendMap);
//        userDbStorage.add(user1);
//
//        var user2 = UserGenerator.createUserWithNextId(jdbcTemplate);
//        user2.setFriends(friendMap);
//        userDbStorage.add(user2);
//
//        // Act
//        var actualFriends = userDbStorage.getCommonFriends(user1.getId(), user2.getId());
//
//        // Assert
//        Assertions.assertEquals(expectedFriends, actualFriends);
//    }

    private HashMap<Integer, Boolean> getUserFriends(int userId) {

        String sqlQuery = "SELECT * FROM FRIENDSHIPS WHERE USER_ID = ?";

        var rs = jdbcTemplate.queryForRowSet(sqlQuery, userId);

        var result = new HashMap<Integer, Boolean>();

        while (rs.next()) {
            result.put(rs.getInt("friend_id"), rs.getBoolean("confirmation"));
        }

        return result;
    }

    private User makeUser(SqlRowSet rs) {
        var birthday = rs.getDate("birthday");
        return new User(
                rs.getInt("USER_ID"),
                rs.getString("EMAIL"),
                rs.getString("LOGIN"),
                rs.getString("USER_NAME"),
                birthday != null ? birthday.toLocalDate() : null,
                getUserFriends(rs.getInt("USER_ID")));
    }
}
