package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;


public interface UserStorage {
    void add(User film);

    Optional<User> update(User film);

    Optional<User> remove(long id);

    Optional<User> getUserById(long id);

    Collection<User> getUsersByIds(Collection<Long> ids);

    Collection<User> getAll();

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByLogin(String login);
}
