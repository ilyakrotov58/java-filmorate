package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.services.UserService;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ArrayList<User> getAll() throws SQLException {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) throws SQLException {
        var user = userService.getUser(id);
        if (user == null) throw new NotFoundException("User with id=" + id + "doesn't exist");
        return user;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("User " + user.getEmail() + " was added");
        return userService.add(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("User " + user.getEmail() + " was updated");
        return userService.update(user);
    }

    @DeleteMapping
    public void deleteUser(@RequestBody User user) {
        log.info("User with id=" + user.getId() + " has been deleted");
        userService.deleteUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("User with id=" + id + " add friend with id=" + friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("User with id=" + id + " delete friend with id=" + friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public ArrayList<User> getFriends(@PathVariable int id) throws SQLException {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ArrayList<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) throws SQLException {
        return userService.getCommonFriends(id, otherId);
    }
}
