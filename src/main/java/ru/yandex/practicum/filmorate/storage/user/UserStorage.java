package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    //Добавить пользователя
    User addUser(User user);

    //Обновить пользователя
    User updateUser(User user);

    //Найти пользователя по id
    User findById(long id);

    //Получить всех пользователей
    Collection<User> findAll();

    //Добавить событие пользователя
    void addEvent(long timestamp, long userId, EventType eventType, Operation operation, long entityId);

    //Удалить пользователя
    void deleteUser(long id);
}
