package ru.yandex.practicum.filmorate.storage.film;

public class FilmSql {
    static final String INSERT_FILM = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES (?,?,?,?,?)";
    static final String FIND_ALL_FILMS = "SELECT * FROM films";
    static final String FIND_FILM_BY_ID = "SELECT * FROM films WHERE id = ?";
    static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE id = ?";
    static final String DELETE_FILM = "DELETE FROM films WHERE id = ?";

    static final String INSERT_GENRE_TO_FILM = "MERGE INTO movie_genres (film_id, genre_id) KEY (film_id,genre_id) VALUES (?,?)";
    static final String GET_GENRE_BY_FILM_ID = """
            SELECT g.id, g.name
            FROM movie_genres AS mg
            JOIN genres AS g ON mg.genre_id = g.id
            WHERE mg.film_id = ?""";

    static final String LIKE_FILM = """
            MERGE INTO likes_movies (film_id, user_id)\s
            KEY(film_id, user_id)\s
            VALUES (?, ?);""";
    static final String DELETE_LIKE = "DELETE FROM likes_movies WHERE film_id = ? AND user_id = ?";
    static final String FIND_POPULAR = """
            SELECT f.*
            FROM films AS f
            LEFT JOIN likes_movies AS lm ON f.id = lm.film_id
            GROUP BY f.id
            ORDER BY COUNT(lm.user_id) DESC
            LIMIT ?""";
    static final String GET_LIKES_BY_FILM_ID = "SELECT user_id FROM likes_movies WHERE film_id = ?";

    static final String CHECK_MPA_ID = "SELECT COUNT(*) FROM mpa_rating WHERE id = ?";
    static final String CHEK_GENRE_ID = "SELECT COUNT(*) FROM genres WHERE id = ?";

    static final String FIND_ALL_MPA = "SELECT * FROM mpa_rating";
    static final String FIND_BY_ID_MPA = "SELECT * FROM mpa_rating WHERE id = ?";

    static final String FIND_ALL_GENRE = "SELECT * FROM genres";
    static final String FIND_BY_ID_GENRE = "SELECT * FROM genres WHERE id = ?";
}
