package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {

    //Добавить отзыв
    Review addReview(Review review);

    //Обновить отзыв
    Review updateReview(Review review);

    //Удалить отзыв
    void deleteReview(long id);

    //Найти отзыв по id
    Review findById(long id);

    //Получить список отзывов
    Collection<Review> findAll(Long filmId, int count);

    //Добавить лайк отзыву
    void addLike(long reviewId, long userId);

    //Добавить дизлайк отзыву
    void addDislike(long reviewId, long userId);

    //Удалить лайк с отзыва
    void deleteLike(long reviewId, long userId);

    //Удалить дизлайк с отзыва
    void deleteDislike(long reviewId, long userId);
}
