package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Arrays;

/*
    Перечисление операций.
    Определяет возможные операции над сущностями: добавление, удаление, обновление.
 */
public enum Operation {
    REMOVE,
    ADD,
    UPDATE;

    public static Operation contains(String text) {
        return Arrays.stream(Operation.values())
                .filter(e -> e.name().equals(text))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Operation не найден"));
    }

}
