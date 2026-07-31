package ru.yandex.practicum.filmorate.storage.film.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.Director;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class UpdateFilmRequest {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private MpaRating mpa;
    private LinkedHashSet<Genre> genres;
    private Set<Director> director = new HashSet<>();

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
        return !(mpa == null || mpa.getId() == 0);
    }

    public boolean hasGenres() {
        return genres != null && !genres.isEmpty();
    }

    public boolean hasDirectors() {
        return director != null && !director.isEmpty();
    }
}
