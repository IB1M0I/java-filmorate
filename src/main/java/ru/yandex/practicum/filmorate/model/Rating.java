package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Rating {
    private Long id;
    private String code;
    private String description;
    private int ageLimit;
}
