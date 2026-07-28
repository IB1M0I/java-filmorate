package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.storage.review.dto.ReviewDto;
import ru.yandex.practicum.filmorate.storage.review.dto.UpdateReviewRequest;

/*
    mapper для преобразования между dto и моделью review.
*/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewMapper {

    //Преобразует review в dto для ответа API.
    public static ReviewDto mapToDto(Review review) {
        ReviewDto dto = new ReviewDto();

        dto.setReviewId(review.getReviewId());
        dto.setContent(review.getContent());
        dto.setIsPositive(review.getIsPositive());
        dto.setUserId(review.getUserId());
        dto.setFilmId(review.getFilmId());
        dto.setUseful(review.getUseful());

        return dto;
    }

    //Преобразует запрос на создание отзыва в review и устанавливает начальный рейтинг в 0.
    public static Review mapToReview(NewReviewRequest request) {
        Review review = new Review();

        review.setContent(request.getContent());
        review.setIsPositive(request.getIsPositive());
        review.setUserId(request.getUserId());
        review.setFilmId(request.getFilmId());
        review.setUseful(0);

        return review;
    }
    //Обновляет review данными из запроса.
    public static Review mapToUpdate(Review review,
                                     UpdateReviewRequest request) {

        if (request.hasContent()) {
            review.setContent(request.getContent());
        }

        if (request.hasIsPositive()) {
            review.setIsPositive(request.getIsPositive());
        }

        return review;
    }
}
