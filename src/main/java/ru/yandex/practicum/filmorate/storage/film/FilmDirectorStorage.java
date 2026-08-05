package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmDirectorStorage {
    Collection<Film> getFilmsByDirectorSorted(long directorId, String sortBy);
}