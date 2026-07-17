package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.h2.command.dml.Set;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.film.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.film.dto.UpdateFilmRequest;

import java.util.HashSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {

    public static FilmDto mapToFilmDto(Film film){
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setDuration(film.getDuration());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setMpa(film.getMpa());
        filmDto.setGenres(film.getGenre());
        filmDto.setLikes(film.getLikes());
        return filmDto;
    }

    public static Film mapToFilm(NewFilmRequest request){
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        if(request.getMpa() != null){
            MpaRating mpaRating = new MpaRating(request.getMpa().getId());
            film.setMpa(mpaRating);
        }else{
            film.setMpa(new MpaRating());
        }
        film.setGenre(request.getGenre());
        film.setLikes(new HashSet<Long>());
        return film;
    }

    public static Film mapToUpdate(Film film, UpdateFilmRequest request) {
        if(request.hasDescription()){
            film.setDescription(request.getDescription());
        }
        if(request.hasDuration()){
            film.setDuration(request.getDuration());
        }
        if(request.hasName()){
            film.setName(request.getName());
        }
        if(request.hasReleaseDate()){
            film.setReleaseDate(request.getReleaseDate());
        }
        if(request.hasMpaRatingId()){
            film.setMpa(new MpaRating(request.getMpa().getId()));
        }
        if(request.hasGenres()){
            film.setGenre(request.getGenres());
        }
        return film;

    }

}
