package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    User findById(long id);

    Collection<User> findAll();

    void addEvent(long timestamp, long userId, EventType eventType, Operation operation, long entityId);

    void deleteUser(long id);
}
