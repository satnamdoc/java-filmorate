package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    public void add(Film film) {
        films.put(film.getId(), film);
    }

    public Optional<Film> update(Film film) {
        return Optional.ofNullable(films.replace(film.getId(), film));
    }

    public Optional<Film> remove(long id) {
        return Optional.ofNullable(films.remove(id));
    }

    public Optional<Film> get(long id) {
        return Optional.ofNullable(films.get(id));
    }

    public Collection<Film> getAll() {
        return List.copyOf(films.values());
    }
}
