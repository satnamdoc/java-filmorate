package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private final Set<String> logins = new HashSet<>();

    public void add(User user) {
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        logins.add(user.getLogin());
    }

    public Optional<User> update(User newUser) {
        User oldUser = users.replace(newUser.getId(), newUser);
        if (oldUser == null) {
            return Optional.empty();
        }
        if (!oldUser.getEmail().equals(newUser.getEmail())) {
            emails.remove(oldUser.getEmail());
            emails.add(newUser.getEmail());
        }
        if (!oldUser.getLogin().equals(newUser.getLogin())) {
            logins.remove(oldUser.getLogin());
            logins.add(newUser.getLogin());
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

    public Optional<User> get(long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Collection<User> getAll() {
        return List.copyOf(users.values());
    }

    public boolean isEmailInUse(String email) {
        return emails.contains(email);
    }

    public boolean isLoginInUse(String login) {
        return logins.contains(login);
    }
}
