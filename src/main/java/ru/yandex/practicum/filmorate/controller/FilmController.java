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

    //Получить фильмы режиссера с сортировкой
    @GetMapping("/director/{directorId}")
    public Collection<FilmDto> getFilmsByDirector(
            @PathVariable long directorId,
            @RequestParam String sortBy) {
        log.debug("Получен запрос на получение фильмов режиссера с id: {}, сортировка: {}",
                directorId, sortBy);
        Collection<FilmDto> films = filmService.getFilmsByDirector(directorId, sortBy);
        log.info("Получено {} фильмов режиссера с id: {}", films.size(), directorId);
        return films;
    }

    /**
     *
     * @param query текст для поиска
     * @param by    может принимать значения director (поиск по режиссёру),
     *              title (поиск по названию),
     *              либо оба значения через запятую при поиске одновременно и по режиссеру и по названию
     * @return возвращает список фильмов, отсортированных по популярности
     */
    @GetMapping("/search")
    public Collection<FilmDto> searchFilmsByTitleByDirector(
            @RequestParam @NotNull String query,
            @RequestParam @NotNull String by
    ) {
        log.debug("Получен запрос на поиск по названию фильмов и по режиссёру {}: {}", by, query);
        Collection<FilmDto> filmsByTitleByDirector = filmService.searchFilmsByTitleByDirector(query, by);
        log.info("Получено {} фильмов по названию и по режиссёру {}: {}", filmsByTitleByDirector.size(), by, query);
        return filmsByTitleByDirector;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable long id) {
        log.debug("Получен запрос на удаление фильма с id: {}", id);
        filmService.deleteFilm(id);
        log.info("Фильм с id {} успешно удален", id);
    }

    @GetMapping("/common")
    @ResponseStatus(HttpStatus.OK)
    public Collection<FilmDto> getCommonFilms(
            @RequestParam long userId,
            @RequestParam long friendId) {
        log.debug("Получен запрос на получение общих фильмов пользователей {} и {}", userId, friendId);
        Collection<FilmDto> films = filmService.getCommonFilms(userId, friendId);
        log.info("Получено {} общих фильмов", films.size());
        return films;
    }

    //Поставить оценку фильму
    @PutMapping("{filmId}/like/{userId}/{rating}")
    public void addRatingFilm(@PathVariable long filmId, @PathVariable long userId, @PathVariable double rating) {
        log.debug("Пользователь {} ставит оценку {} фильму с id: {}", userId, rating, filmId);
        filmService.addRatingFilm(filmId,userId,rating);

    }


}
