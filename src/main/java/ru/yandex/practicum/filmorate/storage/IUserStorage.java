package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.models.User;

import java.sql.SQLException;
import java.util.ArrayList;

public interface IUserStorage {

    ArrayList<User> getAll() throws SQLException;

    User add(User user);

    User update(User user);

    User getUserById(int userId) throws SQLException;

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    void deleteUser(User user);

    ArrayList<User> getFriends(int userId) throws SQLException;

    ArrayList<User> getCommonFriends(int userId, int userIdToCompare) throws SQLException;
}
