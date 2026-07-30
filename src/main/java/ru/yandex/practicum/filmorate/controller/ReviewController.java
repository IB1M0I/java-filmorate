package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.review.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.storage.review.dto.ReviewDto;
import ru.yandex.practicum.filmorate.storage.review.dto.UpdateReviewRequest;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto addReview(@Valid @RequestBody NewReviewRequest request) {
        log.debug("Получен запрос на добавление отзыва от пользователя {}", request.getUserId());
        ReviewDto review = reviewService.addReview(request);
        log.info("Отзыв успешно добавлен с id: {}", review.getReviewId());
        return review;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ReviewDto updateReview(@Valid @RequestBody UpdateReviewRequest request) {
        log.debug("Получен запрос на обновление отзыва с id: {}", request.getReviewId());
        ReviewDto review = reviewService.updateReview(request);
        log.info("Отзыв с id {} успешно обновлен", review.getReviewId());
        return review;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable long id) {
        log.debug("Получен запрос на удаление отзыва с id: {}", id);
        reviewService.deleteReview(id);
        log.info("Отзыв с id {} успешно удален", id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewDto findById(@PathVariable long id) {
        log.debug("Получен запрос на получение отзыва с id: {}", id);
        ReviewDto review = reviewService.findById(id);
        log.info("Отзыв с id {} успешно получен", id);
        return review;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ReviewDto> findAll(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") int count) {
        log.debug("Получен запрос на получение отзывов для фильма: {}", filmId);
        Collection<ReviewDto> reviews = reviewService.findAll(filmId, count);
        log.info("Получено {} отзывов", reviews.size());
        return reviews;
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewDto addLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Пользователь {} ставит лайк отзыву {}", userId, id);
        ReviewDto review = reviewService.addLike(id, userId);
        log.info("Лайк успешно добавлен: пользователь {} отзыву {}", userId, id);
        return review;
    }

    @PutMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewDto addDislike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Пользователь {} ставит дизлайк отзыву {}", userId, id);
        ReviewDto review = reviewService.addDislike(id, userId);
        log.info("Дизлайк успешно добавлен: пользователь {} отзыву {}", userId, id);
        return review;
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewDto deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Пользователь {} удаляет лайк с отзыва {}", userId, id);
        ReviewDto review = reviewService.deleteLike(id, userId);
        log.info("Лайк успешно удален: пользователь {} с отзыва {}", userId, id);
        return review;
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewDto deleteDislike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Пользователь {} удаляет дизлайк с отзыва {}", userId, id);
        ReviewDto review = reviewService.deleteDislike(id, userId);
        log.info("Дизлайк успешно удален: пользователь {} с отзыва {}", userId, id);
        return review;
    }
}

