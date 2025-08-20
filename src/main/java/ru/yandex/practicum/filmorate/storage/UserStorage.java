package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;


public interface UserStorage {
    void add(User film);

    Optional<User> update(User film);

    Optional<User> remove(long id);

    Optional<User> get(long id);

    Collection<User> getAll();

    boolean isEmailInUse(String email);

    boolean isLoginInUse(String login);
}
