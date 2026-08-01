package ru.yandex.practicum.filmorate.storage.review;

public class ReviewSqlQueries {
    private ReviewSqlQueries() {
    }

    public static final String INSERT_REVIEW =
            "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) VALUES (?, ?, ?, ?, ?)";

    public static final String UPDATE_REVIEW =
            "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";

    public static final String DELETE_REVIEW =
            "DELETE FROM reviews WHERE review_id = ?";

    public static final String FIND_BY_ID =
            "SELECT * FROM reviews WHERE review_id = ?";

    public static final String FIND_ALL_BY_FILM =
            "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";

    public static final String FIND_ALL =
            "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";

    public static final String INSERT_LIKE =
            "MERGE INTO review_likes (review_id, user_id, is_like) KEY (review_id, user_id) VALUES (?, ?, ?)";

    public static final String DELETE_LIKE_DISLIKE =
            "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";

    public static final String UPDATE_USEFUL =
            "UPDATE reviews SET useful = (SELECT COUNT(CASE WHEN is_like = true THEN 1 END) - " +
                    "COUNT(CASE WHEN is_like = false THEN 1 END) FROM review_likes WHERE review_id = ?) WHERE review_id = ?";
}
