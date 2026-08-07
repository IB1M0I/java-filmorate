package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;


/*
    Сущность фильма.
    Содержит информацию о фильме: название, описание, дата выхода, длительность,
    жанры, MPA рейтинг, лайки и режиссеры.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private Long id; //ID фильма

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name; //Название фильма

    @Size(max = 200, message = "Описание фильма не может быть более 200 символов")
    private String description; //Описание фильма

    private LocalDate releaseDate; //Дата выпуска фильма

    @Positive(message = "Длительность фильма не может быть 0")
    private Integer duration; //Продолжительность фильма

    @Builder.Default
    private LinkedHashSet<Genre> genres = new LinkedHashSet<>(); //Жанры

    private MpaRating mpa; //MPA рейтинг

    @Min(1)
    @Max(10)
    @JsonProperty("rate")
    private Double rating = 0.0;

    @Builder.Default
    private Set<Director> directors = new HashSet<>();
}
