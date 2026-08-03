package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.film.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.film.dto.UpdateFilmRequest;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@Validated
public class FilmController {
    private final FilmService filmService;

    //Добавить фильм
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto addFilm(@Valid @RequestBody NewFilmRequest request) {
        log.debug("Получен запрос на добавление фильма: {}", request.getName());
        FilmDto film = filmService.addFilm(request);
        log.info("Фильм успешно добавлен с id: {}", film.getId());
        return film;
    }

    //Получить все фильмы
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<FilmDto> findAll() {
        log.debug("Получен запрос на получение всех фильмов");
        Collection<FilmDto> films = filmService.findAll();
        log.info("Получено {} фильмов", films.size());
        return films;
    }

    //Получить фильм по id
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FilmDto findById(@PathVariable long id) {
        log.debug("Получен запрос на получение фильма с id: {}", id);
        FilmDto film = filmService.findById(id);
        log.info("Фильм с id {} успешно получен", id);
        return film;
    }


    //Обновить информацию о фильме
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public FilmDto updateFilm(@Valid @RequestBody UpdateFilmRequest request) {
        log.debug("Получен запрос на обновление фильма с id: {}", request.getId());
        FilmDto film = filmService.updateFilm(request);
        log.info("Фильм с id {} успешно обновлен", film.getId());
        return film;
    }


    //Поставить лайк фильму
    @PutMapping("/{id}/like/{userId}")
    public FilmDto likeFilm(@PathVariable long id, @PathVariable long userId) {
        log.debug("Пользователь {} ставит лайк фильму с id: {}", userId, id);
        FilmDto film = filmService.likeFilm(id, userId);
        log.info("Лайк успешно добавлен: пользователь {} фильму {}", userId, id);
        return film;
    }

    //Удалить лайк с фильма
    @DeleteMapping("/{id}/like/{userId}")
    public FilmDto deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Пользователь {} удаляет лайк с фильма с id: {}", userId, id);
        FilmDto film = filmService.deleteLike(id, userId);
        log.info("Лайк успешно удален: пользователь {} с фильма {}", userId, id);
        return film;
    }

    //Получить список популярных фильмов
    @GetMapping("/popular")
    public Collection<FilmDto> getPopular(
            @RequestParam(defaultValue = "10") @NotNull @Positive Integer count,
            @RequestParam(required = false) @Positive Integer genreId,
            @RequestParam(required = false) @Positive Integer year) {
        String logMsg = "популярных фильмов" +
                (genreId != null ? " жанра с id=" + genreId : "") +
                (year != null ? " " + year + " года" : "");
        log.debug("Получен запрос на получение {} {}", count, logMsg);
        Collection<FilmDto> films = filmService.getPopularFilmsByGenreIdByYear(count, genreId, year);
        log.info("Получено {} {}", films.size(), logMsg);
        return films;
    }

    //Получить список общих фильмов с другом
    @GetMapping("/common")
    public Collection<FilmDto> getCommonFilms(@RequestParam long userId, long friendId) {

        log.debug("Пользователь {} ищет общие фильмы с пользователем {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

}
