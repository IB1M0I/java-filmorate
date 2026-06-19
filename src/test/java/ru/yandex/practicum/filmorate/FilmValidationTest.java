package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

public class FilmValidationTest {
    private Validator validator;
    private FilmController filmController = new FilmController();
    private Set<ConstraintViolation<Film>> validations;


    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void filmNameIsEmpty() {
        Film film = new Film();

        film.setId(1L);
        film.setName("");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        film.setDescription("Описание");

        validations = validator.validate(film);

        Assertions.assertFalse(validations.isEmpty());
        Assertions.assertTrue(validations.stream().anyMatch(v -> v.getMessage().equals("Название фильма не может быть пустым")));
        Assertions.assertTrue(validations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));

    }

    @Test
    public void filmNameIsValid() {
        Film film = new Film();

        film.setId(1L);
        film.setName("Фильм");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        film.setDescription("Описание");

        Film create = filmController.addFilm(film);
        Assertions.assertEquals(film, create);
    }

    @Test
    public void filmDescriptionExceeds200Chars() {
        Film film = new Film();

        film.setId(1L);
        film.setName("Фильм");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        film.setDescription("Описание1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");

        validations = validator.validate(film);

        Assertions.assertFalse(validations.isEmpty());
        Assertions.assertTrue(validations.stream().anyMatch(v -> v.getMessage().equals("Описание фильма не может быть более 200 символов")));
        Assertions.assertTrue(validations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    public void filmDescriptionExactly200Chars() {
        Film film = new Film();

        film.setId(1L);
        film.setName("Фильм");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        film.setDescription("11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");

        Film create = filmController.addFilm(film);
        Assertions.assertEquals(film, create);
    }

    @Test
    public void filmReleaseDateBeforeFirstFilm() {
        Film film = new Film();

        film.setId(1L);
        film.setName("Фильм");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);
        film.setDescription("Описание");

        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    public void filmReleaseDateExactlyFirstFilm() {
        Film film = new Film();

        film.setId(1L);
        film.setName("Фильм");
        film.setReleaseDate(LocalDate.of(1985, 12, 28));
        film.setDuration(120);
        film.setDescription("Описание");

        Film create = filmController.addFilm(film);
        Assertions.assertEquals(film, create);

    }

    @Test
    public void filmDurationIsZero() {
        Film film = new Film();

        film.setId(1L);
        film.setName("Фильм");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(0);
        film.setDescription("Описание");

        validations = validator.validate(film);

        Assertions.assertFalse(validations.isEmpty());
        Assertions.assertTrue(validations.stream().anyMatch(v -> v.getMessage().equals("Длительность фильма не может быть 0")));
        Assertions.assertTrue(validations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }

    @Test
    public void filmDurationIsNegative() {
        Film film = new Film();

        film.setId(1L);
        film.setName("Фильм");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(-1);
        film.setDescription("Описание");

        validations = validator.validate(film);

        Assertions.assertFalse(validations.isEmpty());
        Assertions.assertTrue(validations.stream().anyMatch(v -> v.getMessage().equals("Длительность фильма не может быть 0")));
        Assertions.assertTrue(validations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }

    @Test
    public void filmDurationIsPositive() {
        Film film = new Film();

        film.setId(1L);
        film.setName("Фильм");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(1);
        film.setDescription("Описание");

        Film create = filmController.addFilm(film);

        Assertions.assertEquals(film, create);
    }
}
