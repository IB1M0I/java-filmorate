package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;


@Data
public class Film {
    Long id; //ID фильма

    @NotBlank(message = "Название фильма не может быть пустым")
    String name; //Название фильма

    @Size(max = 200, message = "Описание фильма не может быть более 200 символов")
    String description; //Описание фильма

    LocalDate releaseDate; //Дата выпуска фильма

    @Positive(message = "Длительность фильма не может быть 0")
    Integer duration; //Продолжительность фильма

    Set<Long> likes;

}
