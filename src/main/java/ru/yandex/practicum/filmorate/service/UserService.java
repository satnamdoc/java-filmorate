package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrMsg;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

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

    private static final Pattern EMAIL = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE);

    public Collection<User> findAll() {
        return userStorage.getAll();
    }

    public User findById(long id) {
        return userStorage.getUserById(id).orElseThrow(
                () -> new NotFoundException(User.class.getSimpleName(), String.valueOf(id))
        );
    }

    public User create(User user) {
        validateUser(user);
        if (userStorage.getUserByLogin(user.getLogin()).isPresent()) {
            log.warn(String.format("Ошибка валидации %s. %s", ERR_MSG_LOGIN_IN_USE.getParam(),
                    ERR_MSG_LOGIN_IN_USE.getMsg()));
            throw new ValidationException(ERR_MSG_LOGIN_IN_USE.getParam(), ERR_MSG_LOGIN_IN_USE.getMsg());
        }
        if (userStorage.getUserByEmail(user.getEmail()).isPresent()) {
            log.warn(String.format("Ошибка валидации %s. %s", ERR_MSG_EMAIL_IN_USE.getParam(),
                    ERR_MSG_EMAIL_IN_USE.getMsg()));
            throw new ValidationException(ERR_MSG_EMAIL_IN_USE.getParam(), ERR_MSG_EMAIL_IN_USE.getMsg());
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userStorage.add(user);
        log.info("Пользователь id=" + user.getId() + " создан.");
        return user;
    }


    public User update(User newUser) {
        validateUser(newUser);
        User oldUser = userStorage.getUserById(newUser.getId()).orElseThrow(
                () -> new NotFoundException(User.class.getSimpleName(), String.valueOf(newUser.getId()))
        );

        if (userStorage.getUserByLogin(newUser.getLogin()).orElse(oldUser).getId() !=
                newUser.getId()) {
            log.warn(String.format("Ошибка валидации %s. %s", ERR_MSG_LOGIN_IN_USE.getParam(),
                    ERR_MSG_LOGIN_IN_USE.getMsg()));
            throw new ValidationException(ERR_MSG_LOGIN_IN_USE.getParam(), ERR_MSG_LOGIN_IN_USE.getMsg());
        }
        if (userStorage.getUserByEmail(newUser.getEmail()).orElse(oldUser).getId() !=
                newUser.getId()) {
        log.warn(String.format("Ошибка валидации %s. %s", ERR_MSG_EMAIL_IN_USE.getParam(),
                    ERR_MSG_EMAIL_IN_USE.getMsg()));
            throw new ValidationException(ERR_MSG_EMAIL_IN_USE.getParam(), ERR_MSG_EMAIL_IN_USE.getMsg());
        }

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        newUser.getFriends().addAll(oldUser.getFriends());

        if (userStorage.update(newUser).isPresent()) {
            log.info("Пользователь id=" + newUser.getId() + " обновлен.");
        } else {
            log.info("Ошибка сохранения пользователя id=" + newUser.getId() + ".");
        }
        return newUser;
    }

    public Collection<User> getFriends(long id) {
        return userStorage.getUsersByIds(
                userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException(User.class.getSimpleName(), String.valueOf(id)))
                .getFriends());
    }

    public Collection<User> assignFriend(long id, long friendId) {
        User u1 = userStorage.getUserById(id).orElseThrow(
                () -> new NotFoundException(User.class.getSimpleName(), String.valueOf(id)));
        User u2 = userStorage.getUserById(friendId).orElseThrow(
                () -> new NotFoundException(User.class.getSimpleName(), String.valueOf(friendId)));
        u1.getFriends().add(friendId);
        u2.getFriends().add(id);
        log.info("Пользователи id=" + id + " и id=" + friendId + " стали друзьями.");
        return List.of(u1, u2);
    }

    public Collection<User> removeFromFriends(long id, long friendId) {
        User u1 = userStorage.getUserById(id).orElseThrow(
                () -> new NotFoundException(User.class.getSimpleName(), String.valueOf(id)));
        User u2 = userStorage.getUserById(friendId).orElseThrow(
                () -> new NotFoundException(User.class.getSimpleName(), String.valueOf(friendId)));
        u1.getFriends().remove(friendId);
        u2.getFriends().remove(id);
        log.info("Пользователи id=" + id + " и id=" + friendId + " больше не друзья.");
        return List.of(u1, u2);
    }

    public Collection<User> getCommonFriends(long id1, long id2) {
        User u1 = userStorage.getUserById(id1).orElseThrow(
                () -> new NotFoundException(User.class.getSimpleName(), String.valueOf(id1)));
        User u2 = userStorage.getUserById(id2).orElseThrow(
                () -> new NotFoundException(User.class.getSimpleName(), String.valueOf(id2)));
        return userStorage.getUsersByIds(
                u1.getFriends().stream().filter(u2.getFriends()::contains).toList());
    }

    private static void validateUser(User user) {
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
}
