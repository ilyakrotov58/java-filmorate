package ru.yandex.practicum.filmorate.storage;


import lombok.Getter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class UserDbStorage implements IUserStorage {

    @Getter
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ArrayList<User> getAll() throws SQLException {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users");
        var result = new ArrayList<User>();
        while (userRows.next()) {
            User user = makeUser(userRows);
            result.add(user);
        }
        return result;
    }

    @Override
    public User add(User user) {
        if (user.getName() != null && user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");

        var returnedUserId = (int) simpleJdbcInsert.executeAndReturnKey(user.toMap());
        user.setId(returnedUserId);

        if (user.getFriends() != null && user.getFriends().size() > 0) {
            var userFriends = user.getFriends();
            for (Integer friendId : userFriends.keySet()) {
                String query = "INSERT INTO FRIENDSHIPS VALUES (?, ?, ?)";
                jdbcTemplate.update(query, user.getId(), friendId, userFriends.get(friendId));
            }
        }

        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE users SET email = ?, login = ?, user_name = ?, birthday = ? "
                + "WHERE USER_ID = ?";

        var userId = jdbcTemplate.update(sqlQuery
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
                , user.getId());

        if (userId == 0) {
            throw new NotFoundException("User with id =" + user.getId() + " was not found");
        }

        return user;
    }

    @Override
    public User getUserById(int userId) {
        String sqlQuery = "SELECT * FROM USERS WHERE USER_ID = ?";

        var userRs = jdbcTemplate.queryForRowSet(sqlQuery, userId);

        User user;

        if (userRs.first()) {
            user = makeUser(userRs);
        } else {
            throw new NotFoundException("User with id = " + userId + "was not found");
        }

        return user;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String queryForUser = "SELECT * FROM USERS WHERE USER_ID = ?";
        String queryForFriend = "SELECT * FROM USERS WHERE USER_ID = ?";
        var rsUser = jdbcTemplate.queryForRowSet(queryForUser, userId);
        var rsFriend = jdbcTemplate.queryForRowSet(queryForFriend, friendId);

        if (!rsUser.next()) {
            throw new NotFoundException("Can't find user with id=" + userId);
        } else if (!rsFriend.next()) {
            throw new NotFoundException("Can't find user with id=" + userId);
        }

        String queryForSearching = "SELECT * FROM FRIENDSHIPS WHERE USER_ID = ? AND FRIEND_ID = ?";

        var rs = jdbcTemplate.queryForRowSet(queryForSearching, userId, friendId);

        if (rs.next() && !rs.getBoolean("CONFIRMATION")) {
            jdbcTemplate.update("UPDATE FRIENDSHIPS SET CONFIRMATION = true " +
                    "WHERE USER_ID = ? AND FRIEND_ID = ?", userId, friendId);

            return;
        }

        jdbcTemplate.update("INSERT INTO FRIENDSHIPS VALUES" +
                "(?, ?, ?)", userId, friendId, false);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sqlQuery = "DELETE FROM FRIENDSHIPS WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void deleteUser(User user) {
        String sqlQuery = "DELETE FROM USERS WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, user.getId());
    }

    @Override
    public ArrayList<User> getFriends(int userId) {
        String sqlQuery = "SELECT F.USER_ID, F.FRIEND_ID, EMAIL, LOGIN, USER_NAME, BIRTHDAY" +
                " FROM FRIENDSHIPS AS F" +
                " INNER JOIN USERS AS U ON U.USER_ID = F.FRIEND_ID" +
                " WHERE F.USER_ID = ?";
        var rs = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        var result = new ArrayList<User>();
        while (rs.next()) {
            var user = makeUser(rs);
            user.setFriends(getUserFriends(rs.getInt("FRIEND_ID")));
            user.setId(rs.getInt("FRIEND_ID"));
            result.add(user);
        }

        return result;
    }

    @Override
    public ArrayList<User> getCommonFriends(int userId, int userIdToCompare) {
        String sqlQuery = "SELECT F.FRIEND_ID FROM FRIENDSHIPS " +
                "INNER JOIN FRIENDSHIPS AS F ON F.FRIEND_ID = FRIENDSHIPS.FRIEND_ID " +
                "WHERE FRIENDSHIPS.USER_ID = ? AND F.USER_ID = ?";
        var rs = jdbcTemplate.queryForRowSet(sqlQuery, userId, userIdToCompare);

        var friendsIds = new ArrayList<Integer>();
        while (rs.next()) {
            friendsIds.add(rs.getInt("FRIEND_ID"));
        }

        var result = new ArrayList<User>();

        String queryForFriend = "SELECT * FROM USERS WHERE USER_ID = ?";
        for (Integer friendId : friendsIds) {
            var friendRowSet = jdbcTemplate.queryForRowSet(queryForFriend, friendId);
            if(friendRowSet.next()){
                var friend = makeUser(friendRowSet);
                result.add(friend);
            }
        }

        return result;
    }

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
