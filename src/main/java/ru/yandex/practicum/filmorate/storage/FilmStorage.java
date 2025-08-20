package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    void add(Film film);

    Optional<Film> update(Film film);

    Optional<Film> remove(long id);

    Optional<Film> get(long id);

    Collection<Film> getAll();
}
