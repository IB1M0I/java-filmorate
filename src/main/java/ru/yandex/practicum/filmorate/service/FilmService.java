package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.film.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.film.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    //Добавить новый фильм
    public FilmDto addFilm(NewFilmRequest request) {
        Film film = FilmMapper.mapToFilm(request);
        return FilmMapper.mapToFilmDto(
                filmStorage.addFilm(film)
        );
    }

    //Получить список всех фильмов
    public Collection<FilmDto> findAll() {
        return filmStorage.findAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    //Получить фильм по id
    public FilmDto findById(long id) {
        return FilmMapper.mapToFilmDto(
                filmStorage.findById(id)
        );
    }

    //Обновить информацию о фильме
    public FilmDto updateFilm(UpdateFilmRequest request) {
        Film film = filmStorage.findById(request.getId());

        Film filmUpdate = FilmMapper.mapToUpdate(film, request);
        return FilmMapper.mapToFilmDto(
                filmStorage.updateFilm(filmUpdate)
        );
    }


    //Поставить лайк фильму
    public FilmDto likeFilm(long id, long userId) {
        try {
            filmStorage.findById(id);
        } catch (NotFoundException e) {
            throw new NotFoundException(String.format("Фильм с id = %d не найден, поставить лайк не получилось", id));
        }
        try {
            userStorage.findById(userId);
        } catch (NotFoundException e) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден, поставить лайк не получилось", userId));
        }

        return FilmMapper.mapToFilmDto(
                filmStorage.likeFilm(id, userId)
        );
    }

    //Удалить лайк с фильма
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public FilmDto deleteLike(long id, long userId) {

        try {
            filmStorage.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Фильм с id = %d не найден, удалять нечего", id));
        }
        try {
            userStorage.findById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден, удалять нечего", userId));
        }

        int result = filmStorage.deleteLike(id, userId);
        if (result == 0) {
            throw new NotFoundException(String.format("Лайк от пользователя с id = %d для фильма с id = %d не найден", userId, id));
        }
        return FilmMapper.mapToFilmDto(filmStorage.findById(id));
    }

    //Получить count популярных фильмов по жанру и году
    public Collection<FilmDto> getPopularFilmsByGenreIdByYear(int count, Integer genreId, Integer year) {
        return filmStorage
                .getPopularFilmsByGenreIdByYear(count, genreId, year)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    //Получить список всех рейтингов MPA
    public Collection<MpaRating> findAllMpa() {
        return filmStorage.findAllMpa();
    }

    //Получить рейтинг MPA по id
    public MpaRating findByIdMpa(int id) {
        return filmStorage.findByIdMpa(id);
    }

    //Получить список всех жанров
    public Collection<Genre> findAllGenres() {
        return filmStorage.findAllGenres();
    }

    //Получить жанр по id
    public Genre findGenreById(long id) {
        return filmStorage.findGenreById(id);
    }

    //Получить общие фильмы
    public Collection<FilmDto> getCommonFilms(long userId, long friendId) {
        if (userId == friendId) {
            throw new ValidationException("Оба пользователя указаны с одинаковым id");
        }

        return filmStorage.getCommonFilms(userId, friendId).stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }
}
