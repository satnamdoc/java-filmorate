package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmControllerTest {

    private static FilmController fc;

    @BeforeEach
    public void createController() {
        fc = new FilmController();
    }

    @Test
    public void createFilmWhenReleaseDateAfter18951228() {
        Film film = new Film(1, "name", "desc", LocalDate.parse("1895-12-29"), 1);
        fc.create(film);
        assertEquals(List.of(film), new ArrayList<>(fc.findAll()), "Фильм не добавлен");
    }

    @Test
    public void createFilmWhenReleaseDate18951228() {
        Film film = new Film(1, "name", "desc", LocalDate.parse("1895-12-28"), 1);
        fc.create(film);
        assertEquals(List.of(film), new ArrayList<>(fc.findAll()), "Фильм не добавлен");
    }

    @Test
    public void createShouldFailWhenReleaseDateBefore18951228() {
        Film film = new Film(1, "name", "desc", LocalDate.parse("1895-12-27"), 0);
        assertThrows(ValidationException.class, () -> fc.create(film),
                "Должно быть выброшено исключение некорректной валидации");
        assertTrue(fc.findAll().isEmpty(), "Фильм не должен быть добавлен");
    }
}