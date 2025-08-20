package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = { "email" })
public class User {
    private long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    private final Set<Long> friends = new HashSet<>();
}
