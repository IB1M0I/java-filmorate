package ru.yandex.practicum.filmorate.storage.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/*
    Запрос на создание нового отзыва.
    Содержит поля, необходимые для создания отзыва.
*/
@Data
public class NewReviewRequest {

    @NotBlank(message = "Текст отзыва не может быть пустым")
    private String content;

    @NotNull(message = "Тип отзыва обязателен")
    private Boolean isPositive;

    @NotNull(message = "Не указан пользователь")
    private Long userId;

    @NotNull(message = "Не указан фильм")
    private Long filmId;
}
