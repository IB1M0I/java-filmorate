package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmDirectorStorage {
    //Получить фильмы режиссера с сортировкой
    Collection<Film> getFilmsByDirectorSorted(long directorId, String sortBy);

    //Получить список общих фильмов двух пользователей
    Collection<Film> getCommonFilms(long userId, long friendId);
}