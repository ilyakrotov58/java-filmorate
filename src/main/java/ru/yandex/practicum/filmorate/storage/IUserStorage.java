package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.models.User;

import java.util.ArrayList;
import java.util.HashSet;

public interface IUserStorage {

    ArrayList<User> getAll();

    User add(User user);

    User update(User user);

    HashSet<String> getExistingEmails();

    User getUser(int userId);

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    void deleteUser(User user);

    ArrayList<User> getFriends(int userId);

    ArrayList<User> getCommonFriends(int userId, int userIdToCompare);
}
