package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Event {
    long eventId;
    long timestamp;
    long userId;
    EventType eventType;
    Operation operation;
    long entityId;
}
