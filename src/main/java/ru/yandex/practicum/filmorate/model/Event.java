package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
    Сущность события пользователя.
    Содержит информацию о действиях пользователей (лайки, отзывы, друзья).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    long eventId;
    long timestamp;
    long userId;
    EventType eventType;
    Operation operation;
    long entityId;
}
