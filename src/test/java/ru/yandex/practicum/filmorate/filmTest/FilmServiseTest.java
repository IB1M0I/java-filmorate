package ru.yandex.practicum.filmorate.filmTest;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.film.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;


public class FilmServiseTest {
    private final FilmService filmService;

    @BeforeEach
    public void setUp(){
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);
    }
    @Test
    public void filmReleaseDateBeforeCinemaBirthdayThrowsException(){
        NewFilmRequest film = NewFilmRequest.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.of(1895,12,27))
                .genre(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();

        Assertions.assertThatThrownBy(()-> filmService.addFilm(film)).isInstanceOf(ValidationException.class);
    }
}
