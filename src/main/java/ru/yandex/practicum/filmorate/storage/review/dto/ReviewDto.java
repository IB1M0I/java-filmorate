package ru.yandex.practicum.filmorate.storage.review.dto;

import lombok.Data;

/*
    Передачи данных отзыва через API.
    Содержит все поля модели Review для отобажения пользователю.
*/
@Data
public class ReviewDto {

    private Long reviewId;

    private String content;

    private Boolean isPositive;

    private Long userId;

    private Long filmId;

    private Integer useful;
}
