package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        return userService.update(newUser);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(
            @PathVariable long id) {
        return userService.getFriends(id);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public Collection<User> assignFriend(
            @PathVariable long userId, @PathVariable long friendId) {
        return userService.assignFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public Collection<User> removeFromFriends(
            @PathVariable long userId, @PathVariable long friendId) {
        return userService.removeFromFriends(userId, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(
            @PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}