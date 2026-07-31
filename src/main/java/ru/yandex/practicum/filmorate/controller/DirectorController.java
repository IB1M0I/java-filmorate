package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    //Создание режиссера
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director create(@Valid @RequestBody Director director) {
        log.debug("Получен запрос на создание режиссера: {}", director.getName());
        Director created = directorService.create(director);
        log.info("Режиссер успешно создан с id: {}", created.getId());
        return created;
    }

    //Получение всех режиссеров
    @GetMapping
    public Collection<Director> findAll() {
        log.debug("Получен запрос на получение всех режиссеров");
        Collection<Director> directors = directorService.findAll();
        log.info("Получено {} режиссеров", directors.size());
        return directors;
    }

    //Получение режиссера по ID
    @GetMapping("/{id}")
    public Director findById(@PathVariable long id) {
        log.debug("Получен запрос на получение режиссера с id: {}", id);
        Director director = directorService.findById(id);
        log.info("Режиссер с id {} успешно получен", id);
        return director;
    }

    //Обновление режиссера
    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        log.debug("Получен запрос на обновление режиссера с id: {}", director.getId());
        Director updated = directorService.update(director);
        log.info("Режиссер с id {} успешно обновлен", updated.getId());
        return updated;
    }

    //Удаление режиссера
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        log.debug("Получен запрос на удаление режиссера с id: {}", id);
        directorService.delete(id);
        log.info("Режиссер с id {} успешно удален", id);
    }
}
