package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {

    //Создать режиссера
    Director create(Director director);

    //Обновить режиссера
    Director update(Director director);

    //Найти режиссера по id
    Director findById(long id);

    //Получить всех режиссеров
    Collection<Director> findAll();

    //Удалить режиссера
    void delete(long id);
}
