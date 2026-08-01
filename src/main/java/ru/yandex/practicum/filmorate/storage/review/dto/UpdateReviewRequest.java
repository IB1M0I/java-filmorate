package ru.yandex.practicum.filmorate.storage.review.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/*
    Запрос на обновление существующего отзыва.
    Содержит идентификатор отзыва и поля для обновления.
*/
@Data
public class UpdateReviewRequest {

    @NotNull(message = "review ID не может быть пустым")
    private Long reviewId;

    private String content;

    private Boolean isPositive;

    public boolean hasContent() {
        return content != null && !content.isBlank();
    }

    public boolean hasIsPositive() {
        return isPositive != null;
    }
}