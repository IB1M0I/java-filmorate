package ru.yandex.practicum.filmorate.storage.film.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
public class NewFilmRequest {
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Описание фильма не может быть более 200 символов")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Длительность фильма не может быть 0")
    private int duration;
    private LinkedHashSet<Genre> genres;
    private Set<Long> likes;
    private MpaRating mpa;
}
