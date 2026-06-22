package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Comparator;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public ResponseEntity<byte[]> likeFilm(long id, long userId) {
        log.trace("Вызван метод likeFilm");

        if (filmStorage.findById(id) == null) {
            log.error("Фильм с id = {} не найден, поставить лайк не получилось", id);
            throw new NotFoundException(String.format("Фильм с id = %d не найден, поставить лайк не получилось", id));
        }

        if (userStorage.findById(userId) == null) {
            log.error("Пользователь с id = {} не найден, поставить лайк не получилось", userId);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден, поставить лайк не получилось", userId));
        }

        Film film = filmStorage.findById(id);
        if (film.getLikes().contains(userId)) {
            log.info("Пользователь " + userId + " уже оставил лайк для фильма " + film.getName());
            return new ResponseEntity<>(String.format("Вы уже оставили лайк для фильма %s", film.getName()).getBytes(StandardCharsets.UTF_8), HttpStatus.CONFLICT);
        }
        film.getLikes().add(userId);
        log.info("Фильму " + film.getName() + " добавлен лайк");
        return new ResponseEntity<>(String.format("Фильму %s добавлен лайк", film.getName()).getBytes(StandardCharsets.UTF_8), HttpStatus.OK);
    }

    public ResponseEntity<byte[]> deleteLike(long id, long userId) {
        log.trace("Вызван метод removeLike");

        if (filmStorage.findById(id) == null) {
            log.error("Фильм с id = {} не найден, удалять нечего", id);
            throw new NotFoundException(String.format("Фильм с id = %d не найден, удалять нечего", id));
        }

        Film film = filmStorage.findById(id);
        if (!film.getLikes().contains(userId)) {
            return new ResponseEntity<>(String.format("Вы еще не оценивали фильм %s", film.getName()).getBytes(StandardCharsets.UTF_8), HttpStatus.CONFLICT);
        }

        film.getLikes().remove(userId);
        return new ResponseEntity<>(String.format("Вы удалили свою оценку фильму %s", film.getName()).getBytes(StandardCharsets.UTF_8), HttpStatus.OK);
    }

    public Collection<Film> getPopular(int count) {
        log.trace("Вызван метод getPopular");

        Comparator<Film> comparator = Comparator.comparing((Film film) -> film.getLikes().size());
        return filmStorage.findAll().stream()
                .sorted(comparator.reversed())
                .limit(count)
                .toList();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public ResponseEntity<byte[]> deleteFilm(long id) {
        return filmStorage.deleteFilm(id);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(long id) {
        return filmStorage.findById(id);
    }

}
