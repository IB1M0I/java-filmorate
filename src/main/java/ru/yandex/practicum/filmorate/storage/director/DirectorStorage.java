package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {
    Director create(Director director);
    Director update(Director director);
    Director findById(long id);
    Collection<Director> findAll();
    void delete(long id);
}
