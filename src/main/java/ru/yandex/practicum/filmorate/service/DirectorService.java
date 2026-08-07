package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    //Создать режиссера
    public Director create(Director director) {
        log.debug("Создание режиссера: {}", director.getName());
        Director created = directorStorage.create(director);
        log.info("Режиссер успешно создан с id: {}", created.getId());
        return created;
    }

    //Обновить режиссера
    public Director update(Director director) {
        log.debug("Обновление режиссера с id: {}", director.getId());
        Director updated = directorStorage.update(director);
        log.info("Режиссер с id {} успешно обновлен", updated.getId());
        return updated;
    }

    //Получить всех режиссеров
    public Collection<Director> findAll() {
        log.debug("Получение всех режиссеров");
        Collection<Director> directors = directorStorage.findAll();
        log.info("Получено {} режиссеров", directors.size());
        return directors;
    }

    //Получить режиссера по id
    public Director findById(long id) {
        log.debug("Получение режиссера с id: {}", id);
        Director director = directorStorage.findById(id);
        log.info("Режиссер с id {} успешно получен", id);
        return director;
    }

    //Удалить режиссера
    public void delete(long id) {
        log.debug("Удаление режиссера с id: {}", id);
        directorStorage.delete(id);
        log.info("Режиссер с id {} успешно удален", id);
    }
}