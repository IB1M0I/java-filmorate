package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.review.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.storage.review.dto.ReviewDto;
import ru.yandex.practicum.filmorate.storage.review.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewStorage;
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    //Добавить отзыв на фильм
    public ReviewDto addReview(NewReviewRequest request) {
        log.debug("Добавление отзыва от пользователя {} на фильм {}", request.getUserId(), request.getFilmId());
        userStorage.findById(request.getUserId());
        filmStorage.findById(request.getFilmId());

        Review review = ReviewMapper.mapToReview(request);
        ReviewDto reviewDto = ReviewMapper.mapToDto(reviewStorage.addReview(review));
        log.info("Отзыв успешно добавлен с id: {}", reviewDto.getReviewId());
        return reviewDto;
    }

    //Обновить отзыв
    public ReviewDto updateReview(UpdateReviewRequest request) {
        log.debug("Обновление отзыва с id: {}", request.getReviewId());
        Review review = reviewStorage.findById(request.getReviewId());
        Review updatedReview = ReviewMapper.mapToUpdate(review, request);
        ReviewDto reviewDto = ReviewMapper.mapToDto(reviewStorage.updateReview(updatedReview));
        log.info("Отзыв с id {} успешно обновлен", reviewDto.getReviewId());
        return reviewDto;
    }

    //Удалить отзыв
    public void deleteReview(long id) {
        log.debug("Удаление отзыва с id: {}", id);
        reviewStorage.findById(id);
        reviewStorage.deleteReview(id);
        log.info("Отзыв с id {} успешно удален", id);
    }

    //Получить отзыв по id
    public ReviewDto findById(long id) {
        log.debug("Получение отзыва с id: {}", id);
        ReviewDto reviewDto = ReviewMapper.mapToDto(reviewStorage.findById(id));
        log.info("Отзыв с id {} успешно получен", id);
        return reviewDto;
    }

    //Получить список отзывов
    public Collection<ReviewDto> findAll(Long filmId, int count) {
        log.debug("Получение отзывов для фильма: {}, количество: {}", filmId, count);
        Collection<ReviewDto> reviews = reviewStorage.findAll(filmId, count).stream().map(ReviewMapper::mapToDto).toList();
        log.info("Получено {} отзывов", reviews.size());
        return reviews;
    }

    //Добавить лайк отзыву
    public ReviewDto addLike(long reviewId, long userId) {
        log.debug("Пользователь {} ставит лайк отзыву {}", userId, reviewId);
        reviewStorage.findById(reviewId);
        userStorage.findById(userId);
        reviewStorage.addLike(reviewId, userId);
        ReviewDto reviewDto = ReviewMapper.mapToDto(reviewStorage.findById(reviewId));
        log.info("Лайк успешно добавлен: пользователь {} отзыву {}", userId, reviewId);
        return reviewDto;
    }

    //Добавить дизлайк отзыву
    public ReviewDto addDislike(long reviewId, long userId) {
        log.debug("Пользователь {} ставит дизлайк отзыву {}", userId, reviewId);
        reviewStorage.findById(reviewId);
        userStorage.findById(userId);
        reviewStorage.addDislike(reviewId, userId);
        ReviewDto reviewDto = ReviewMapper.mapToDto(reviewStorage.findById(reviewId));
        log.info("Дизлайк успешно добавлен: пользователь {} отзыву {}", userId, reviewId);
        return reviewDto;
    }

    //Удалить лайк с отзыва
    public ReviewDto deleteLike(long reviewId, long userId) {
        log.debug("Пользователь {} удаляет лайк с отзыва {}", userId, reviewId);
        reviewStorage.findById(reviewId);
        userStorage.findById(userId);
        reviewStorage.deleteLike(reviewId, userId);
        ReviewDto reviewDto = ReviewMapper.mapToDto(reviewStorage.findById(reviewId));
        log.info("Лайк успешно удален: пользователь {} с отзыва {}", userId, reviewId);
        return reviewDto;
    }

    //Удалить дизлайк с отзыва
    public ReviewDto deleteDislike(long reviewId, long userId) {
        log.debug("Пользователь {} удаляет дизлайк с отзыва {}", userId, reviewId);
        reviewStorage.findById(reviewId);
        userStorage.findById(userId);
        reviewStorage.deleteDislike(reviewId, userId);
        ReviewDto reviewDto = ReviewMapper.mapToDto(reviewStorage.findById(reviewId));
        log.info("Дизлайк успешно удален: пользователь {} с отзыва {}", userId, reviewId);
        return reviewDto;
    }
}
