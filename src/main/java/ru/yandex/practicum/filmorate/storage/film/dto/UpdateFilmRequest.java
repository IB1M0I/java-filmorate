package ru.yandex.practicum.filmorate.storage.film.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
public class UpdateFilmRequest {
//    @NotNull
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private MpaRating mpa;
    private LinkedHashSet<Genre> genres;

    public boolean hasName() {
        return name != null || !name.isBlank();
    }

    public boolean hasDescription() {
        return description != null || !description.isBlank();
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return duration != null;
    }

    public boolean hasMpaRatingId() {
        return !(mpa.getId() == 0 || mpa == null);
    }

    public boolean hasGenres() {
        return genres != null && !genres.isEmpty();
    }
}
