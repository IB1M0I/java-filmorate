package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;


@Data
public class Film {
    Long id; //ID фильма

    @NotBlank(message = "Название фильма не может быть пустым")
    String name; //Название фильма

    @Size(max = 200, message = "Описание фильма не может быть более 200 символов")
    String description; //Описание фильма

    LocalDate releaseDate; //Дата выпуска фильма

    @Positive
    Integer duration; //Продолжительность фильма


}
