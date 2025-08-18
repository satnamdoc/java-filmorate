package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = { "id" })
public class Film {
    private long id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @Size(min = 0, max = 200, message = "Максимальная длина описания - 200 символов")
    private String  description;
    @PastOrPresent(message = "Некорректная дата")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
}
