package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements IUserStorage {

    private static final Map<Integer, User> users = new HashMap<>();

    private static final Set<String> existingEmails = new HashSet<>();
    private static int nextId = 0;

    @Override
    public ArrayList<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User add(User user) {
        user.setId(setNextId());
        users.put(user.getId(), user);
        existingEmails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getId() <= 0) {
            throw new ValidationException("Can't update user with id=" + user.getId());
        }
        if (users.containsKey(user.getId())) {
            users.replace(user.getId(), user);
            existingEmails.add(user.getEmail());
        }
        return user;
    }

    @Override
    public User getUser(int userId) {
        return users.get(userId);
    }

    @Override
    public void deleteUser(User user) {
        users.remove(user.getId());
    }

    @Override
    public void addFriend(int userId, int friendId) {
        var user = users.get(userId);
        user.getFriends().add(friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        var user = users.get(userId);
        user.getFriends().remove(friendId);
    }

    @Override
    public ArrayList<User> getFriends(int userId) {
        var user = users.get(userId);
        var listOfFriendsIds = user.getFriends();
        var result = new ArrayList<User>();

        for (Integer friendId : listOfFriendsIds) {
            result.add(users.get(friendId));
        }
        return result;
    }

    @Override
    public ArrayList<User> getCommonFriends(int userId, int userIdToCompare) {
        var user = users.get(userId);
        var secondUser = users.get(userIdToCompare);
        var listOfCommonFriendsIds = new HashSet<Integer>();
        for (Integer friendId : user.getFriends()) {
            for (Integer friendIdSecondUser : secondUser.getFriends()) {
                if (friendId.equals(friendIdSecondUser)) {
                    listOfCommonFriendsIds.add(friendId);
                }
            }
        }
        var result = new ArrayList<User>();

        for (Integer friendId : listOfCommonFriendsIds) {
            result.add(users.get(friendId));
        }
        return result;
    }

    private int setNextId() {
        return ++nextId;
    }

    public Set<String> getExistingEmails() {
        return existingEmails;
    }

    // Temporary methods. Will be deleted after we will have real db in project

    public static void setStartId0() {
        nextId = 0;
    }

    public static void clearDb() {
        users.clear();
        existingEmails.clear();
    }
}
