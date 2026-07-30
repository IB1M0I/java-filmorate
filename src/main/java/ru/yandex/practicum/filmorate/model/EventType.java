package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.exeption.NotFoundException;

import java.util.Arrays;

public enum EventType {
    LIKE,
    REVIEW,
    FRIEND;

    public static EventType contains(String text) {
        return Arrays.stream(EventType.values())
                .filter(e -> e.name().equals(text))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("EventType не найден"));
    }
}
