package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.IUserStorage;

import java.util.ArrayList;
import java.util.HashSet;

@Service
public class UserService {

    @Autowired
    IUserStorage userStorage;

    public ArrayList<User> getAll() {
        return userStorage.getAll();
    }

    public User add(User user) {
        return userStorage.add(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public HashSet<String> getExistingEmails() {
        return userStorage.getExistingEmails();
    }

    public User getUser(int userId) {
        return userStorage.getUser(userId);
    }

    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
        userStorage.addFriend(friendId, userId);
    }

    public void deleteFriend(int userId, int friendId) {
        userStorage.deleteFriend(userId, friendId);
        userStorage.deleteFriend(friendId, userId);
    }

    public void deleteUser(User user) {
        userStorage.deleteUser(user);
    }

    public ArrayList<User> getFriends(int userId) {
        return userStorage.getFriends(userId);
    }

    public ArrayList<User> getCommonFriends(int userId, int userIdToCompare) {
        return userStorage.getCommonFriends(userId, userIdToCompare);
    }
}
