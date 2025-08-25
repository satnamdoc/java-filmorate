package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);


    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{filmId}")
    public Film findById(@PathVariable long filmId) {
        return filmService.findById(filmId);
    }

    @GetMapping("/popular")
    public Collection<Film> findTopRated(
            @RequestParam(defaultValue = "10") int count) {
        return filmService.findTopRated(count);
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Map<String, Long> like(
            @PathVariable long filmId, @PathVariable long userId) {
        return Map.of("total likes", filmService.like(filmId, userId));
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Map<String, Long> unlike(
            @PathVariable long filmId, @PathVariable long userId) {
        return Map.of("total likes", filmService.unlike(filmId, userId));
    }
}
