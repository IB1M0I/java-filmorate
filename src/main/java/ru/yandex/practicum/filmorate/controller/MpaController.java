package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    private final FilmService filmService;

    @GetMapping
    public Collection<MpaRating> findAllMpa() {
        log.debug("Получен запрос на получение всех рейтингов MPA");
        Collection<MpaRating> mpaRatings = filmService.findAllMpa();
        log.info("Получено {} рейтингов MPA", mpaRatings.size());
        return mpaRatings;
    }

    @GetMapping("/{id}")
    public MpaRating findByIdMpa(@PathVariable int id) {
        log.debug("Получен запрос на получение рейтинга MPA с id: {}", id);
        MpaRating mpaRating = filmService.findByIdMpa(id);
        log.info("Рейтинг MPA с id {} успешно получен", id);
        return mpaRating;
    }
}
