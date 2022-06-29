package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {

    private static final HashMap<Integer, User> users = new HashMap<>();
    private static final HashSet<String> existingEmails = new HashSet<>();
    private static int nextId = 0;

    @GetMapping
    public ArrayList<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user, HttpServletResponse response) throws IOException {
        try {
            validateId(user);
            validateUserName(user);
            validateEmail(user);
            user.setId(setNextId());
            users.put(user.getId(), user);
            existingEmails.add(user.getEmail());
        } catch (AlreadyExistException ex) {
            response.sendError(400);
            log.error(ex.getMessage());
        }
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user, HttpServletResponse response) throws IOException {
        try {
            if (users.containsKey(user.getId())) {
                validateUserName(user);
                validateEmptyEmail(user);
                users.replace(user.getId(), user);
                existingEmails.add(user.getEmail());
            } else {
                response.sendError(500);
            }
        } catch (ValidationException | javax.validation.ValidationException ex) {
            log.error(ex.getMessage());
            response.sendError(400);
        }
        return user;
    }

    private int setNextId() {
        return ++nextId;
    }

    private void validateUserName(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    private void validateEmail(User user) throws AlreadyExistException {
        if (existingEmails.contains(user.getEmail())) {
            throw new AlreadyExistException("User with email " + user.getEmail() + " is already exist");
        }
    }

    private void validateEmptyEmail(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new ValidationException("Can't update user with empty email");
        }
    }

    private void validateId(User user) {
        if (!users.containsKey(user.getId()) && user.getId() != 0) {
            throw new AlreadyExistException("User with id " + user.getId() + " already exist");
        }
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
