package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.film.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.film.dto.UpdateFilmRequest;

import java.util.LinkedHashSet;
import java.util.HashSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {

    //Преобразовать сущность Film в DTO
    public static FilmDto mapToFilmDto(Film film) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setDuration(film.getDuration());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setMpa(film.getMpa());
        filmDto.setGenres(film.getGenres());
        filmDto.setRating(film.getRating());
        filmDto.setDirectors(film.getDirectors());
        return filmDto;
    }

    //Преобразовать запрос на создание фильма в сущность Film
    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        if (request.getMpa() != null) {
            MpaRating mpaRating = new MpaRating(request.getMpa().getId());
            film.setMpa(mpaRating);
        } else {
            film.setMpa(new MpaRating());
        }
        film.setGenres(request.getGenres());
        if(request.getRating() != null){
            film.setRating(request.getRating());
        }
        if (request.getDirectors() != null) {
            film.setDirectors(request.getDirectors());
        } else {
            film.setDirectors(new HashSet<>());
        }
        return film;
    }

    //Обновить сущность Film данными из запроса на обновление
    public static Film mapToUpdate(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }
        if (request.hasMpaRatingId()) {
            film.setMpa(new MpaRating(request.getMpa().getId()));
        }
        if (request.getGenres() != null) {
            film.setGenres(request.getGenres());
        } else {
            film.setGenres(new LinkedHashSet<>());
        }
        if (request.getDirectors() != null) {
            film.setDirectors(request.getDirectors());
        } else {
            film.setDirectors(new HashSet<>());
        }
        if(request.hasRating()){
            film.setRating(request.getRating());
        }
        return film;

    }

}
