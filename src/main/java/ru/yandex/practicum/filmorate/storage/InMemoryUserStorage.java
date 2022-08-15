package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import java.util.*;

@Component
@Qualifier("InMemoryUserStorage")
@Deprecated
public class InMemoryUserStorage implements IUserStorage {

    private static final Map<Integer, User> users = new HashMap<>();
    private static int nextId = 0;

    @Override
    public ArrayList<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User add(User user) {
        user.setId(setNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getId() <= 0) {
            throw new ValidationException("Can't update user with id=" + user.getId());
        }
        if (users.containsKey(user.getId())) {
            users.replace(user.getId(), user);
        }
        return user;
    }

    @Override
    public User getUserById(int userId) {
        return users.get(userId);
    }

    @Override
    public void deleteUser(User user) {
        users.remove(user.getId());
    }

    @Override
    public void addFriend(int userId, int friendId) {
        var user = users.get(userId);
        var friend = users.get(friendId);

        if(friend.getFriends().get(userId) == null){
                user.getFriends().put(friendId, false);
                friend.getFriends().put(userId, false);
        }
        else if(!friend.getFriends().get(userId)){
            user.getFriends().replace(friendId, true);
            friend.getFriends().replace(userId, true);
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        var user = users.get(userId);
        user.getFriends().remove(friendId);
    }

    @Override
    public ArrayList<User> getFriends(int userId) {
        var user = users.get(userId);
        var listOfFriendsIds = user.getFriendsIds(true);
        var result = new ArrayList<User>();

        for (Integer friendId : listOfFriendsIds) {
            result.add(users.get(friendId));
        }
        return result;
    }

    @Override
    public ArrayList<User> getCommonFriends(int userId, int friendId) {
        var user = users.get(userId);
        var friend = users.get(friendId);
        var listOfCommonFriendsIds = new HashSet<Integer>();
        for (Integer confirmedFriendId : user.getFriendsIds(true)) {
            for (Integer friendIdSecondUser : friend.getFriendsIds(true)) {
                if (confirmedFriendId.equals(friendIdSecondUser)) {
                    listOfCommonFriendsIds.add(friendId);
                }
            }
        }
        var result = new ArrayList<User>();

        for (Integer id : listOfCommonFriendsIds) {
            result.add(users.get(id));
        }
        return result;
    }

    private int setNextId() {
        return ++nextId;
    }

    // Temporary methods. Will be deleted after we will have real db in project

    public static void setStartId0() {
        nextId = 0;
    }

    public static void clearDb() {
        users.clear();
    }
}
