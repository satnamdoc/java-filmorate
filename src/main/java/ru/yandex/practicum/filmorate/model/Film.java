package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = { "id" })
public class Film {
    private long id;
    private String name;
    private String  description;
    private LocalDate releaseDate;
    private int duration;

    private final Set<Long> likes = new HashSet<>();
}
