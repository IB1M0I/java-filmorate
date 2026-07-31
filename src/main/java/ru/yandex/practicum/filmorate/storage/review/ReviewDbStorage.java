package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<Review> rowMapper;

    private static final String INSERT_REVIEW =
            "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_REVIEW =
            "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
    private static final String DELETE_REVIEW =
            "DELETE FROM reviews WHERE review_id = ?";
    private static final String FIND_BY_ID =
            "SELECT * FROM reviews WHERE review_id = ?";
    private static final String FIND_ALL_BY_FILM =
            "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
    private static final String FIND_ALL =
            "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";

    private static final String INSERT_LIKE =
            "MERGE INTO review_likes (review_id, user_id, is_like) KEY (review_id, user_id) VALUES (?, ?, ?)";
    private static final String DELETE_LIKE_DISLIKE =
            "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";

    private static final String UPDATE_USEFUL =
            "UPDATE reviews SET useful = (SELECT COUNT(CASE WHEN is_like = true THEN 1 END) - " +
                    "COUNT(CASE WHEN is_like = false THEN 1 END) FROM review_likes WHERE review_id = ?) WHERE review_id = ?";

    @Override
    public Review addReview(Review review) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_REVIEW, Statement.RETURN_GENERATED_KEYS);
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
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        jdbc.update(UPDATE_REVIEW, review.getContent(), review.isPositive(), review.getReviewId());
        return findById(review.getReviewId());
    }

    @Override
    public void deleteReview(long id) {
        jdbc.update(DELETE_REVIEW, id);
    }

    @Override
    public Review findById(long id) {
        try {
            return jdbc.queryForObject(FIND_BY_ID, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Отзыв с id = " + id + " не найден");
        }
    }

    @Override
    public Collection<Review> findAll(Long filmId, int count) {
        if (filmId != null) {
            return jdbc.query(FIND_ALL_BY_FILM, rowMapper, filmId, count);
        } else {
            return jdbc.query(FIND_ALL, rowMapper, count);
        }
    }

    @Override
    public void addLike(long reviewId, long userId) {
        jdbc.update(INSERT_LIKE, reviewId, userId, true);
        jdbc.update(UPDATE_USEFUL, reviewId, reviewId);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        jdbc.update(INSERT_LIKE, reviewId, userId, false);
        jdbc.update(UPDATE_USEFUL, reviewId, reviewId);
    }

    @Override
    public void deleteLike(long reviewId, long userId) {
        jdbc.update(DELETE_LIKE_DISLIKE, reviewId, userId);
        jdbc.update(UPDATE_USEFUL, reviewId, reviewId);
    }

    @Override
    public void deleteDislike(long reviewId, long userId) {
        jdbc.update(DELETE_LIKE_DISLIKE, reviewId, userId);
        jdbc.update(UPDATE_USEFUL, reviewId, reviewId);
    }
}