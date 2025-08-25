package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String objectName;
    private final String property;

    public NotFoundException(String objectName, String property) {
        this.objectName = objectName;
        this.property = property;
    }
}