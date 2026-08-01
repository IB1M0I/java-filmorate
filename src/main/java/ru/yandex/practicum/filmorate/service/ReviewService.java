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

    public ReviewDto addReview(NewReviewRequest request) {
        userStorage.findById(request.getUserId());
        filmStorage.findById(request.getFilmId());

        Review review = ReviewMapper.mapToReview(request);
        return ReviewMapper.mapToDto(reviewStorage.addReview(review));
    }

    public ReviewDto updateReview(UpdateReviewRequest request) {
        Review review = reviewStorage.findById(request.getReviewId());
        Review updatedReview = ReviewMapper.mapToUpdate(review, request);
        return ReviewMapper.mapToDto(reviewStorage.updateReview(updatedReview));
    }

    public void deleteReview(long id) {
        reviewStorage.findById(id);
        reviewStorage.deleteReview(id);
    }

    public ReviewDto findById(long id) {
        return ReviewMapper.mapToDto(reviewStorage.findById(id));
    }

    public Collection<ReviewDto> findAll(Long filmId, int count) {
        return reviewStorage.findAll(filmId, count).stream().map(ReviewMapper::mapToDto).toList();
    }

    public ReviewDto addLike(long reviewId, long userId) {
        reviewStorage.findById(reviewId);
        userStorage.findById(userId);
        reviewStorage.addLike(reviewId, userId);
        return ReviewMapper.mapToDto(reviewStorage.findById(reviewId));
    }

    public ReviewDto addDislike(long reviewId, long userId) {
        reviewStorage.findById(reviewId);
        userStorage.findById(userId);
        reviewStorage.addDislike(reviewId, userId);
        return ReviewMapper.mapToDto(reviewStorage.findById(reviewId));
    }

    public ReviewDto deleteLike(long reviewId, long userId) {
        reviewStorage.findById(reviewId);
        userStorage.findById(userId);
        reviewStorage.deleteLike(reviewId, userId);
        return ReviewMapper.mapToDto(reviewStorage.findById(reviewId));
    }

    public ReviewDto deleteDislike(long reviewId, long userId) {
        reviewStorage.findById(reviewId);
        userStorage.findById(userId);
        reviewStorage.deleteDislike(reviewId, userId);
        return ReviewMapper.mapToDto(reviewStorage.findById(reviewId));
    }
}
