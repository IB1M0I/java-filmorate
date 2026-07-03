package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class Film {
    private Long id; //ID фильма

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name; //Название фильма

    @Size(max = 200, message = "Описание фильма не может быть более 200 символов")
    private String description; //Описание фильма

    private LocalDate releaseDate; //Дата выпуска фильма

    @Positive(message = "Длительность фильма не может быть 0")
    private Integer duration; //Продолжительность фильма

    private Set<Genre> genre = new HashSet<>(); //Жанры

    private MpaRating mpaRating; //MPA рейтинг

    private Set<Long> likes = new HashSet<>(); //Лайки

}
