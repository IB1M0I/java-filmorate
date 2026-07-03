package ru.yandex.practicum.filmorate.model;


import lombok.Getter;
import ru.yandex.practicum.filmorate.exeption.ValidationException;

@Getter
public enum MpaRating {
    G(1, "G"),
    PG(2, "PG"),
    PG_13(3, "PG-13"),
    R(4, "R"),
    NC_17(5, "NC-17");

    private final int id;
    private final String name;

    MpaRating(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static MpaRating fromId(int id) {
        for (MpaRating rating : MpaRating.values()) {
            if (rating.getId() == id) {
                return rating;
            }
        }
        throw new ValidationException("Неверный ID рейтинга: " + id);
    }
}
