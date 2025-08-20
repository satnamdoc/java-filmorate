package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrMsg;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private long lastId = 0;

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    public Collection<Film> findAll() {
        return filmStorage.getAll();
    }

    public Film findById(long id) {
        return filmStorage.get(id).orElseThrow(
                () -> new NotFoundException("Film", String.valueOf(id))
        );
    }

    public Collection<Film> findTopRated(int count) {
        return filmStorage.getAll().stream()
                //.sorted(Comparator.comparing(f -> f.getLikes().size()).reserved())
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }

    public Film create(Film film) {
        validateFilm(film);
        film.setId(getNextId());
        filmStorage.add(film);
        log.info("Фильм id=" + film.getId() + " создан.");
        return film;
    }

    public Film update(Film newFilm) {
        validateFilm(newFilm);
        Film oldFilm = filmStorage.get(newFilm.getId()).orElseThrow(
                () -> new NotFoundException("Film", String.valueOf(newFilm.getId()))
        );
        newFilm.getLikes().addAll(oldFilm.getLikes());
        filmStorage.update(newFilm);
        log.info("Фильм id=" + newFilm.getId() + " обновлён.");
        return newFilm;
    }

    public long like(long filmId, long userId) {
        Film film = filmStorage.get(filmId).orElseThrow(
                () -> new NotFoundException("Film", String.valueOf(filmId))
        );
        userStorage.get(userId).orElseThrow(
                () -> new NotFoundException("User", String.valueOf(userId))
        );

        film.getLikes().add(userId);
        filmStorage.update(film);
        log.info("Пользователь id=" + userId + " поставил лайк фильму id=" + filmId + ".");
        return film.getLikes().size();
    }

    public long unlike(long filmId, long userId) {
        Film film = filmStorage.get(filmId).orElseThrow(
                () -> new NotFoundException("Film", String.valueOf(filmId))
        );
        userStorage.get(userId).orElseThrow(
                () -> new NotFoundException("User", String.valueOf(userId))
        );

        film.getLikes().remove(userId);
        filmStorage.update(film);
        log.info("Пользователь id=" + userId + " убрал лайк фильма id=" + filmId + ".");
        return film.getLikes().size();
    }

    private void validateFilm(Film film) {
        final LocalDate CINEMA_BIRTHDATE = LocalDate.parse("1895-12-28");

        final ErrMsg ERR_MSG_NAME_IS_BLANK = new ErrMsg(
                "Film.name",
                "Имя не может быть пустым.");
        final ErrMsg ERR_MSG_MAX_DESC_LENGTH = new ErrMsg(
                "Film.dsescription",
                "Максимальная длина описания - 200 символов.");
        final ErrMsg ERR_MSG_RELEASE_DATE_FROM_FUTURE = new ErrMsg(
                "Film.releaseDate",
                "Дата релиза должна быть в прошлом.");
        final ErrMsg ERR_MSG_RELEASE_DATE_TOO_EARLY = new ErrMsg(
                "Film.releaseDate",
                "Дата релиза должна быть не раньше 28 декабря 1895 года.");
        final ErrMsg ERR_MSG_INCORRECT_DURATION = new ErrMsg(
                "Film.duration",
                "Продолжительность фильма должна быть положительным числом.");

        ErrMsg errMsg = null;
        if (film.getName() == null || film.getName().isBlank()) {
            errMsg = ERR_MSG_NAME_IS_BLANK;
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            errMsg = ERR_MSG_MAX_DESC_LENGTH;
        } else if (film.getReleaseDate() != null && film.getReleaseDate().isAfter(LocalDate.now())) {
            errMsg = ERR_MSG_RELEASE_DATE_FROM_FUTURE;
        } else if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(CINEMA_BIRTHDATE)) {
            errMsg = ERR_MSG_RELEASE_DATE_TOO_EARLY;
        } else if (film.getDuration() <= 0) {
            errMsg = ERR_MSG_INCORRECT_DURATION;
        }

        if (errMsg != null) {
            log.warn(String.format("Ошибка валидации %s. %s", errMsg.getParam(), errMsg.getMsg()));
            throw new ValidationException(errMsg.getParam(), errMsg.getMsg());
        }
        log.trace("Валидации объекта Film прошла успешно.");
    }

    private long getNextId() {
        return ++lastId;
    }
}
