package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.Collection;

public interface FilmStorage {
    //Добавить фильм
    Film addFilm(Film film);

    //Обновить фильм
    Film updateFilm(Film film);

    //Найти фильм по id
    Film findById(long id);

    //Получить все фильмы
    Collection<Film> findAll();

    //Добавить событие пользователя
    void addEvent(long timestamp, long userId, EventType eventType, Operation operation, long entityId);

    //Удалить фильм
    void deleteFilm(long id);

}
