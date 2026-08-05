package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.Instant;
import java.util.Collection;


@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<Review> rowMapper;

    @Override
    public Review addReview(Review review) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(ReviewSqlQueries.INSERT_REVIEW, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.isPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            ps.setInt(5, review.getUseful());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            review.setReviewId(id);
        } else {
            throw new RuntimeException("Не удалось сохранить отзыв и получить id");
        }
        addEvent(Instant.now().toEpochMilli(), review.getUserId(), EventType.REVIEW, Operation.ADD, review.getReviewId());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        int row = jdbc.update(ReviewSqlQueries.UPDATE_REVIEW, review.getContent(), review.isPositive(), review.getReviewId());

        if (row > 0) {
            addEvent(Instant.now().toEpochMilli(), review.getUserId(), EventType.REVIEW, Operation.UPDATE, review.getReviewId());
            return findById(review.getReviewId());
        } else {
            throw new NotFoundException("не удалось обновить отзыв");
        }
    }

    @Override
    public void deleteReview(long id) {
        Review review = findById(id);
        int row = jdbc.update(ReviewSqlQueries.DELETE_REVIEW, id);
        if (row > 0) {
            addEvent(Instant.now().toEpochMilli(), review.getUserId(), EventType.REVIEW, Operation.REMOVE, id);
        } else {
            throw new NotFoundException("не удалось удалить отзыв");
        }
    }

    @Override
    public Review findById(long id) {
        try {
            return jdbc.queryForObject(ReviewSqlQueries.FIND_BY_ID, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Отзыв с id = " + id + " не найден");
        }
    }

    @Override
    public Collection<Review> findAll(Long filmId, int count) {
        if (filmId != null) {
            return jdbc.query(ReviewSqlQueries.FIND_ALL_BY_FILM, rowMapper, filmId, count);
        } else {
            return jdbc.query(ReviewSqlQueries.FIND_ALL, rowMapper, count);
        }
    }

    @Override
    public void addLike(long reviewId, long userId) {
        jdbc.update(ReviewSqlQueries.INSERT_LIKE, reviewId, userId, true);
        jdbc.update(ReviewSqlQueries.UPDATE_USEFUL, reviewId, reviewId);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        jdbc.update(ReviewSqlQueries.INSERT_LIKE, reviewId, userId, false);
        jdbc.update(ReviewSqlQueries.UPDATE_USEFUL, reviewId, reviewId);
    }

    @Override
    public void deleteLike(long reviewId, long userId) {
        jdbc.update(ReviewSqlQueries.DELETE_LIKE_DISLIKE, reviewId, userId);
        jdbc.update(ReviewSqlQueries.UPDATE_USEFUL, reviewId, reviewId);
    }

    @Override
    public void deleteDislike(long reviewId, long userId) {
        jdbc.update(ReviewSqlQueries.DELETE_LIKE_DISLIKE, reviewId, userId);
        jdbc.update(ReviewSqlQueries.UPDATE_USEFUL, reviewId, reviewId);
    }

    //Добавить событие
    public void addEvent(long timestamp, long userId, EventType eventType, Operation operation, long entityId) {
        jdbc.update(ReviewSqlQueries.INSERT_USER_EVENT, timestamp, userId, eventType.name(), operation.name(), entityId);
    }
}