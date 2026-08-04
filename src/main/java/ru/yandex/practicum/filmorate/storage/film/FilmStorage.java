package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.Collection;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);


    Film findById(long id);

    Collection<Film> findAll();

    void addEvent(long timestamp, long userId, EventType eventType, Operation operation, long entityId);

}
