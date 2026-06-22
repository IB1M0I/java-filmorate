package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    ResponseEntity<byte[]> deleteFilm(long id);

    Film findById(long id);

    Collection<Film> findAll();

}
