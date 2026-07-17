package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.film.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.film.dto.UpdateFilmRequest;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    //Добавить фильм
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto addFilm(@Valid @RequestBody NewFilmRequest request) {
        return filmService.addFilm(request);
    }

    //Получить все фильмы
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    //Получить фильм по id
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FilmDto findById(@PathVariable long id) {
        return filmService.findById(id);
    }


    //Обновить информацию о фильме
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public FilmDto updateFilm(@Valid @RequestBody UpdateFilmRequest request) {
        return filmService.updateFilm(request);
    }

    //Удалить фильм
    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable long id) {
        filmService.deleteFilm(id);
    }

    //Поставить лайк фильму
    @PutMapping("/{id}/like/{userId}")
    public FilmDto likeFilm(@PathVariable long id, @PathVariable long userId) {
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable long id, @PathVariable long userId) {
        return filmService.deleteLike(id, userId);
    }
    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopular(count);
    }





}
