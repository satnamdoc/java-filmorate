package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrMsg;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    private long lastId = 0;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private static final ErrMsg ERR_MSG_WRONG_EMAIL = new ErrMsg(
            "User.email",
            "Некорректный email.");
    private static final ErrMsg ERR_MSG_EMAIL_IN_USE = new ErrMsg(
            "User.email",
            "Почта уже используется.");
    private static final ErrMsg ERR_MSG_BLANK_LOGIN = new ErrMsg(
            "User.login",
            "Логин не может быть пустым.");
    private static final ErrMsg ERR_MSG_LOGIN_IN_USE = new ErrMsg(
            "User.login",
            "Логин уже используется.");
    private static final ErrMsg ERR_MSG_NO_SPACES = new ErrMsg(
            "User.login",
            "Логин не должен содержать пробелы.");
    private static final ErrMsg ERR_MSG_BAD_BIRTHDAY = new ErrMsg(
            "User.birthday",
            "Некорректная дата.");

    public Collection<User> findAll() {
        return userStorage.getAll();
    }

    public User findById(long id) {
        return userStorage.get(id).orElseThrow(
                () -> new NotFoundException("User", String.valueOf(id))
        );
    }

    public User create(@RequestBody User user) {
        validateUser(user);
        if (userStorage.isLoginInUse(user.getLogin())) {
            log.warn(String.format("Ошибка валидации %s. %s", ERR_MSG_LOGIN_IN_USE.getParam(),
                    ERR_MSG_LOGIN_IN_USE.getMsg()));
            throw new ValidationException(ERR_MSG_LOGIN_IN_USE.getParam(), ERR_MSG_LOGIN_IN_USE.getMsg());
        }
        if (userStorage.isEmailInUse(user.getEmail())) {
            log.warn(String.format("Ошибка валидации %s. %s", ERR_MSG_EMAIL_IN_USE.getParam(),
                    ERR_MSG_EMAIL_IN_USE.getMsg()));
            throw new ValidationException(ERR_MSG_EMAIL_IN_USE.getParam(), ERR_MSG_EMAIL_IN_USE.getMsg());
        }

        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userStorage.add(user);
        log.info("Пользователь id=" + user.getId() + " создан.");
        return user;
    }

    public User update(@RequestBody User newUser) {
        validateUser(newUser);
        User oldUser = userStorage.get(newUser.getId()).orElseThrow(
                () -> new NotFoundException("User", String.valueOf(newUser.getId()))
        );
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        newUser.getFriends().addAll(oldUser.getFriends());

        log.info("Пользователь id=" + oldUser.getId() + " обновлен.");
        return userStorage.update(newUser).get();
    }

    public Collection<User> getFriends(long id) {
        return userStorage.get(id)
                .orElseThrow(() -> new NotFoundException("User", String.valueOf(id)))
                .getFriends().stream()
                .map(userStorage::get)
                .map(Optional::get)
                .toList();
    }

    public Collection<User> assignFriend(long id, long friendId) {
        User u1 = userStorage.get(id).orElseThrow(
                () -> new NotFoundException("User", String.valueOf(id)));
        User u2 = userStorage.get(friendId).orElseThrow(
                () -> new NotFoundException("User", String.valueOf(friendId)));
        u1.getFriends().add(friendId);
        u2.getFriends().add(id);
        log.info("Пользователи id=" + id + " и id=" + friendId + " стали друзьями.");
        return List.of(u1, u2);
    }

    public Collection<User> removeFromFriends(long id, long friendId) {
        User u1 = userStorage.get(id).orElseThrow(
                () -> new NotFoundException("User", String.valueOf(id)));
        User u2 = userStorage.get(friendId).orElseThrow(
                () -> new NotFoundException("User", String.valueOf(friendId)));
        u1.getFriends().remove(friendId);
        u2.getFriends().remove(id);
        log.info("Пользователи id=" + id + " и id=" + friendId + " больше не друзья.");
        return List.of(u1, u2);
    }

    public Collection<User> getCommonFriends(long id1, long id2) {
        User u1 = userStorage.get(id1).orElseThrow(
                () -> new NotFoundException("User", String.valueOf(id1)));
        User u2 = userStorage.get(id2).orElseThrow(
                () -> new NotFoundException("User", String.valueOf(id2)));

        return u1.getFriends().stream()
                .filter(u2.getFriends()::contains)
                .map(userStorage::get)
                .map(Optional::get)
                .toList();
    }

    private void validateUser(User user) {
        final Pattern EMAIL = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        ErrMsg errMsg = null;
        if (!EMAIL.matcher(user.getEmail()).matches()) {
            errMsg = ERR_MSG_WRONG_EMAIL;
        } else if (user.getLogin() == null || user.getLogin().isBlank()) {
            errMsg = ERR_MSG_BLANK_LOGIN;
        } else if (user.getLogin().contains(" ")) {
            errMsg = ERR_MSG_NO_SPACES;
        } else if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            errMsg = ERR_MSG_BAD_BIRTHDAY;
        }

        if (errMsg != null) {
            log.warn(String.format("Ошибка валидации %s. %s", errMsg.getParam(), errMsg.getMsg()));
            throw new ValidationException(errMsg.getParam(), errMsg.getMsg());
        }
        log.trace("Валидации объекта User прошла успешно.");
    }

    private long getNextId() {
        return ++lastId;
    }
}
