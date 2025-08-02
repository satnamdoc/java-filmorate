package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private static UserController uc;

    @BeforeEach
    public void createController() {
        uc = new UserController();
    }

    @Test
    public void shouldFailWithEmailDuplicate() {
        User user1 = new User(1, "1@1.1", "l1", "n1", LocalDate.now());
        User user2 = new User(2, "1@1.1", "l2", "n2", LocalDate.now());
        uc.create(user1);

        assertThrows(ValidationException.class, () -> uc.create(user2),
                "Должно быть выброшено исключение некорректной валидации");
        assertEquals(List.of(user1), new ArrayList<>(uc.findAll()),
                "Пользователь не должен быть добавлен.");
    }

    @Test
    public void shouldFailWithLoginDuplicate() {
        User user1 = new User(1, "1@1.1", "l1", "n1", LocalDate.now());
        User user2 = new User(2, "2@2.2", "l1", "n2", LocalDate.now());
        uc.create(user1);

        assertThrows(ValidationException.class, () -> uc.create(user2),
                "Должно быть выброшено исключение некорректной валидации");
        assertEquals(List.of(user1), new ArrayList<>(uc.findAll()),
                "Пользователь не должен быть добавлен.");
    }

    @Test
    public void shouldFailWhenLoginIncludesSpaces() {
        User user = new User(1, "1@1.1", "l 1", "n1", LocalDate.now());
        assertThrows(ValidationException.class, () -> uc.create(user),
                "Должно быть выброшено исключение некорректной валидации");
        assertTrue(uc.findAll().isEmpty(), "Пользователь не должен быть добавлен");
    }
}