package ru.yandex.practicum.filmorate.model;

import java.util.Arrays;

public enum Operation {
    REMOVE,
    ADD,
    UPDATE;

    public static Operation contains(String text) {
        return Arrays.stream(Operation.values())
                .filter(e -> e.name().equals(text))
                .findFirst()
                .orElse(null);
    }

}
