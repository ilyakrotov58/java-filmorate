package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.services.UserService;

import javax.validation.Valid;
import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ArrayList<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        validateIfUserExist(id, true);
        return userService.getUser(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        validateIfUserExist(user.getId(), false);
        validateExistingEmail(user);
        validateUserName(user);
        log.info("User " + user.getEmail() + "was added");
        return userService.add(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validateIfUserExist(user.getId(), true);
        validateEmail(user);
        validateUserName(user);
        log.info("User " + user.getEmail() + "was updated");
        return userService.update(user);
    }

    @DeleteMapping
    public void deleteUser(@RequestBody User user) {
        validateIfUserExist(user.getId(), true);
        log.info("User with id=" + user.getId() + " has been deleted");
        userService.deleteUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        validateIfUserExist(id, true);
        validateIfUserExist(friendId, true);
        log.info("User with id=" + id + " add friend with id=" + friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        validateIfUserExist(id, true);
        validateIfUserExist(friendId, true);
        log.info("User with id=" + id + " delete friend with id=" + friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public ArrayList<User> getFriends(@PathVariable int id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ArrayList<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        validateIfUserExist(id, true);
        validateIfUserExist(otherId, true);
        return userService.getCommonFriends(id, otherId);
    }

    private void validateEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new ValidationException("Can't update user with empty email");
        }
    }

    private void validateUserName(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    private void validateExistingEmail(User user) {
        if (userService.getExistingEmails().contains(user.getEmail())) {
            throw new AlreadyExistException("User with email " + user.getEmail() + " is already exist");
        }
    }

    private void validateIfUserExist(int userId, boolean ifUserShouldExist) {
        if (ifUserShouldExist) {
            if (userService.getUser(userId) == null || userId < 0) {
                throw new NotFoundException("User with id=" + userId + " is not exist");
            }
        } else {
            if (userService.getUser(userId) != null) {
                throw new AlreadyExistException("User with id=" + userId + " already exist");
            }
        }
    }
}
