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

    private final Map<Long, Film> films = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

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
        if (films.containsKey(newFilm.getId())) {
            validateFilm(newFilm);
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Фильм id=" + oldFilm.getId() + " обновлен.");
            return oldFilm;
        }
        String errMsg = "Фильм с id=" + newFilm.getId() + " не найден.";
        log.warn(errMsg);
        throw new NotFoundException(errMsg);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate() != null
                && film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            String errMsg = "Дата релиза должна быть не раньше 28 декабря 1895 года.";
            log.warn("Ошибка валидации объекта Film. " + errMsg);
            throw new ValidationException(errMsg);
        }
        log.trace("Валидации объекта Film прошла успешно.");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
