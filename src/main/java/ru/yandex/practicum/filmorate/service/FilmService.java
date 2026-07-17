package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
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

    public FilmDto addFilm(NewFilmRequest request) {
        Film film = FilmMapper.mapToFilm(request);
        return FilmMapper.mapToFilmDto(
                filmStorage.getLikesAndGenresByFilmId(filmStorage.addFilm(film))
        );
    }

    public Collection<FilmDto> findAll() {
        return filmStorage.findAll().stream()
                .map(filmStorage::getLikesAndGenresByFilmId)
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public FilmDto findById(long id) {
        return FilmMapper.mapToFilmDto(
                filmStorage.getLikesAndGenresByFilmId(filmStorage.findById(id))
        );
    }

    public FilmDto updateFilm(UpdateFilmRequest request) {
        Film film = filmStorage.findById(request.getId());

        Film filmUpdate = FilmMapper.mapToUpdate(film, request);
        return FilmMapper.mapToFilmDto(
                filmStorage.getLikesAndGenresByFilmId(filmStorage.updateFilm(filmUpdate))
        );
    }

    public void deleteFilm(long id) {
        filmStorage.deleteFilm(id);
    }

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
                filmStorage.getLikesAndGenresByFilmId(filmStorage.likeFilm(id, userId))
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Film deleteLike(long id, long userId) {

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
        return filmStorage.getLikesAndGenresByFilmId(filmStorage.findById(id));
    }

    public Collection<Film> getPopular(int count) {
        return filmStorage.getLikesAndGenresByFilmId(filmStorage.getPopular(count));
    }

    public Collection<MpaRating> findAllMpa() {
        return filmStorage.findAllMpa();
    }

    public MpaRating findByIdMpa(int id) {
        return filmStorage.findByIdMpa(id);
    }

    public Collection<Genre> findAllGenres() {
        return filmStorage.findAllGenres();
    }

    public Genre findGenreById(long id) {
    return filmStorage.findGenreById(id);
    }
}
