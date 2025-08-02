package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private long lastId = 0;
    private final Map<Long, Film> films = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private static final String ERR_MSG_FILM_NOT_FOUND = "Фильм с id=%d не найден.";
    private static final String ERR_MSG_RELEASE_DATE_TOO_EARLY = "Дата релиза должна быть не раньше 28 декабря 1895 года.";

    private static final LocalDate CINEMA_BIRTHDATE = LocalDate.parse("1895-12-28");

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм id=" + film.getId() + " создан.");
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        if (!films.containsKey(newFilm.getId())) {
            log.warn(ERR_MSG_FILM_NOT_FOUND.formatted(newFilm.getId()));
            throw new NotFoundException(ERR_MSG_FILM_NOT_FOUND.formatted(newFilm.getId()));
        }
        validateFilm(newFilm);
        Film oldFilm = films.get(newFilm.getId());
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        log.info("Фильм id=" + oldFilm.getId() + " обновлен.");
        return oldFilm;
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate() != null
                && film.getReleaseDate().isBefore(CINEMA_BIRTHDATE)) {
            log.warn("Ошибка валидации объекта Film. " + ERR_MSG_RELEASE_DATE_TOO_EARLY);
            throw new ValidationException(ERR_MSG_RELEASE_DATE_TOO_EARLY);
        }
        log.trace("Валидации объекта Film прошла успешно.");
    }

    private long getNextId() {
        return ++lastId;
    }

}
