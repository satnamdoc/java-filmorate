package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private final Set<String> logins = new HashSet<>();

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validateUser(user);
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        emails.add(user.getEmail());
        logins.add(user.getLogin());
        log.info("Пользователь id=" + user.getId() + " создан.");
        return user;
    }

    @PutMapping
    public User update(@Valid@RequestBody User newUser) {
        if (users.containsKey(newUser.getId())) {
            validateUser(newUser);
            User oldUser = users.get(newUser.getId());
            if (!oldUser.getEmail().equals(newUser.getEmail())) {
                emails.remove(oldUser.getEmail());
                emails.add(newUser.getEmail());
                oldUser.setEmail(newUser.getEmail());
            }
            if (!oldUser.getLogin().equals(newUser.getLogin())) {
                logins.remove(oldUser.getLogin());
                logins.add(newUser.getLogin());
                oldUser.setLogin(newUser.getLogin());
            }
            if (newUser.getName().isEmpty() || newUser.getName().isBlank()) {
                oldUser.setName(newUser.getLogin());
            } else {
                oldUser.setName(newUser.getName());
            }
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Пользователь id=" + oldUser.getId() + " обновлен.");
            return oldUser;
        }
        String errMsg = "Пользователь с id=" + newUser.getId() + " не найден.";
        log.warn(errMsg);
        throw new NotFoundException(errMsg);
    }

    private void validateUser(User user) {
        String errMsg = null;
        if (emails.contains(user.getEmail())) {
            errMsg = "Почта " + user.getEmail() + " уже используется.";
        } else if (logins.contains(user.getLogin())) {
            errMsg = "Логин " + user.getLogin() + " уже используется.";
        } else if (user.getLogin().contains(" "))  {
            errMsg = "Логин не должен содержать пробелы.";
        }
        if (errMsg != null) {
            log.warn("Ошибка валидации объекта User. " + errMsg);
            throw new ValidationException(errMsg);
        }
        log.trace("Валидации объекта User прошла успешно.");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
