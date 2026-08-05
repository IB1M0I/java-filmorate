package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
    Отзывы на фильм.
    Содержит информацию о тексте отзыва, его типе (позитивный/негативный), авторе, фильме и рейтинге полезности.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    private Long reviewId;

    private String content;

    private boolean isPositive;

    private Long userId;

    private Long filmId;

    @Builder.Default
    private Integer useful = 0;


}
