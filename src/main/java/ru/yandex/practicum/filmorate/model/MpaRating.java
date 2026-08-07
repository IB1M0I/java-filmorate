package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/*
    Сущность рейтинга MPA.
    Содержит информацию о возрастном рейтинге фильма.
 */
@Getter
@NoArgsConstructor(force = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
@ToString
public class MpaRating {
    private int id;

    @EqualsAndHashCode.Exclude
    private String name;

    public MpaRating(int id) {
        this.id = id;
    }

    public MpaRating(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
