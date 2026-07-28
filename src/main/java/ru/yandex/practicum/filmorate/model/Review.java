package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
    Отзывы на фильм.
    Солержит информацию о тексте отзыва, его типе (позитивный/негативный), авторе, фильме и рейтинге полезности.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    private Long reviewId;

    @NotBlank(message = "Текст отзыва не может быть пустым")
    private String content;

    private Boolean isPositive;

    private Long userId;

    private Long filmId;

    private Integer useful = 0;
}
