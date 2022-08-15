package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.IUserStorage;

import java.sql.SQLException;
import java.util.ArrayList;

@Service
public class UserService {

    private final IUserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") IUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public ArrayList<User> getAll() throws SQLException {
        return userStorage.getAll();
    }

    public User add(User user) {
        return userStorage.add(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User getUser(int userId) throws SQLException {
        return userStorage.getUserById(userId);
    }

    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

    public void deleteUser(User user) {
        userStorage.deleteUser(user);
    }

    public ArrayList<User> getFriends(int userId) throws SQLException {
        return userStorage.getFriends(userId);
    }

    public ArrayList<User> getCommonFriends(int userId, int userIdToCompare) throws SQLException {
        return userStorage.getCommonFriends(userId, userIdToCompare);
    }
}
