package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    Map<Long, Film> films = new HashMap<>();


    //Возвращает список всех фильмов
    @GetMapping
    public Collection<Film> getFilms() {
        log.trace("Вызван метод получения списка фильма");
        return films.values();
    }

    //Добавить фильм
    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.trace("Вызван метод добавления фильма");
        log.debug("film = {}", film);

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата выпуска фильма не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата выпуска фильма не может быть раньше 28 декабря 1895 года");
        }

        film.setId(getNextId());
        log.trace("Фильму {} присвоен id = {}", film.getName(), film.getId());
        films.put(film.getId(), film);
        log.info("В список добавлен новый фильм: {} с id {}", film.getName(), film.getId());
        return film;
    }

    //Обновить фильм
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.trace("Вызван метод обновления фильма");
        log.debug("film = {}", film);

        if (film.getId() == null) {
            log.error("id не может быть пустым");
            throw new ValidationException("id не может быть пустым");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата выпуска фильма не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата выпуска фильма не может быть раньше 28 декабря 1895 года");
        }
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id = {} не найдет", film.getId());
            throw new ValidationException(String.format("Фильм с %d не найдет", film.getId()));
        }

        Film oldFilm = films.get(film.getId());
        log.trace("Найден фильм для обновления данных");

        log.trace("Обновление данных фильма с id = {}", oldFilm.getId());
        oldFilm.setName(film.getName() != null ? film.getName() : oldFilm.getName());
        oldFilm.setDescription(film.getDescription() != null ? film.getDescription() : oldFilm.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate() != null ? film.getReleaseDate() : oldFilm.getReleaseDate());
        oldFilm.setDuration(film.getDuration() != null ? film.getDuration() : oldFilm.getDuration());

        log.info("Фильм с id = {} обновлен", film.getId());

        return oldFilm;
    }

    private long getNextId() {
        long currentId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        log.trace("Сгенерирован новый id");
        return ++currentId;
    }
}
