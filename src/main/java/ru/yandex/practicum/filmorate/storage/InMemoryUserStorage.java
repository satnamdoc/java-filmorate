package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.StorageException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private long lastId = 0;

    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> emails = new HashMap<>();
    private final Map<String, User> logins = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);

    public void add(User user) {
        if (logins.containsKey(user.getLogin())) {
            String err = String.format("Логин %s используется пользователем id=%s",
                    user.getLogin(), logins.get(user.getLogin()).getId());
            log.warn(err);
            throw new StorageException(err);
        }
        if (emails.containsKey(user.getEmail())) {
            String err = String.format("Email %s используется пользователем id=%s",
                    user.getEmail(), emails.get(user.getEmail()).getId());
            log.warn(err);
            throw new StorageException(err);
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        emails.put(user.getEmail(), user);
        logins.put(user.getLogin(), user);
    }

    public Optional<User> update(User newUser) {
        if (logins.containsKey(newUser.getLogin())
                && logins.get(newUser.getLogin()).getId() != newUser.getId()) {
            String err = String.format("Логин %s используется пользователем id=%s",
                    newUser.getLogin(), logins.get(newUser.getLogin()).getId());
            log.warn(err);
            throw new StorageException(err);
        }
        if (emails.containsKey(newUser.getEmail())
                && emails.get(newUser.getLogin()).getId() != newUser.getId()) {
            String err = String.format("Email %s используется пользователем id=%s",
                    newUser.getEmail(), emails.get(newUser.getEmail()).getId());
            log.warn(err);
            throw new StorageException(err);
        }

        User oldUser = users.replace(newUser.getId(), newUser);
        if (oldUser == null) {
            return Optional.empty();
        }
        if (!oldUser.getEmail().equals(newUser.getEmail())) {
            emails.remove(oldUser.getEmail());
            emails.put(newUser.getEmail(), newUser);
        }
        if (!oldUser.getLogin().equals(newUser.getLogin())) {
            logins.remove(oldUser.getLogin());
            logins.put(newUser.getLogin(), newUser);
        }
        return Optional.of(newUser);
    }

    public Optional<User> remove(long id) {
        User user = users.remove(id);
        if (user == null) {
            return Optional.empty();
        }
        emails.remove(user.getEmail());
        logins.remove(user.getLogin());
        return Optional.of(user);
    }

    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Collection<User> getUsersByIds(Collection<Long> ids) {
        return ids.stream().map(users::get).filter(Objects::nonNull).toList();
    }

    public Collection<User> getAll() {
        return List.copyOf(users.values());
    }

    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(emails.get(email));
    }

    public Optional<User> getUserByLogin(String login) {
        return Optional.ofNullable(logins.get(login));
    }

    private long getNextId() {
        return ++lastId;
    }

}
