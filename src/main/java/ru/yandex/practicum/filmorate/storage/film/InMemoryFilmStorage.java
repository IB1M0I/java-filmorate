package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    Map<Long, Film> films = new HashMap<>();

    //Добавить фильм
    @Override
    public Film addFilm(@Valid Film film) {
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


    //Обновить информацию о фильме
    @Override
    public Film updateFilm(Film film) {
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
            throw new NotFoundException(String.format("Фильм с %d не найдет", film.getId()));
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

    //Удалить фильм
    @Override
    public void deleteFilm(long id) {
        log.trace("Вызван метод удаления фильма");
        log.debug("id = {}", id);

        if (!films.containsKey(id)) {
            log.error("Фильм с id = {} не найден", id);
            throw new NotFoundException(String.format("Фильм с id = %d не найден", id));
        }

        films.remove(id);
        log.info("Фильма с id = {} удален", id);
    }

    //Получить все фильмы
    @Override
    public Film findById(long id) {
        log.trace("Вызван метод findById");
        if (!films.containsKey(id)) {
            log.error("Фильм с id = {} не найден", id);
            throw new NotFoundException(String.format("Фильм с id = %d не найден", id));
        }

        return films.get(id);
    }

    //Получить фильм по id
    @Override
    public Collection<Film> findAll() {
        log.trace("Вызван метод получения списка фильма");
        return films.values();

    }

    //Генерации id
    private long getNextId() {
        long currentId = films.keySet().stream().mapToLong(id -> id).max().orElse(0L);
        log.trace("Сгенерирован новый id");
        return ++currentId;
    }
}
