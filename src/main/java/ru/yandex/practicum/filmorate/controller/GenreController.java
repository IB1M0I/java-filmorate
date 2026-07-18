package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Genre> findAll() {
        log.debug("Получен запрос на получение всех жанров");
        Collection<Genre> genres = filmService.findAllGenres();
        log.info("Получено {} жанров", genres.size());
        return genres;
    }

    @GetMapping("/{id}")
    public Genre findById(@PathVariable long id) {
        log.debug("Получен запрос на получение жанра с id: {}", id);
        Genre genre = filmService.findGenreById(id);
        log.info("Жанр с id {} успешно получен", id);
        return genre;
    }
}
