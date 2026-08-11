package ru.yandex.practicum.filmorate.storage.film;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FilmSql {


    //SQL-запрос для добавления фильма
    static final String INSERT_FILM = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id,rating) VALUES (?,?,?,?,?,?)";

    //SQL-запрос для получения всех фильмов
    static final String FIND_ALL_FILMS = "SELECT * FROM films";

    //SQL-запрос для поиска фильма по id
    static final String FIND_FILM_BY_ID = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, " +
            "COALESCE(AVG(r.rating), 0.0) AS calculated_rating " +
            "FROM films f " +
            "LEFT JOIN rating_movies r ON f.id = r.film_id " +
            "WHERE f.id = ? " +
            "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id";

    //SQL-запрос для обновления фильма
    static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ?, rating = ? WHERE id = ?";

    //SQL-запрос для добавления жанра к фильму
    static final String INSERT_GENRE_TO_FILM = "MERGE INTO movie_genres (film_id, genre_id) KEY (film_id,genre_id) VALUES (?,?)";

    //SQL-запрос для удаления фильма
    static final String DELETE_FILM = "DELETE FROM films WHERE id = ?";

    //SQL-запрос для добавления рейтинга фильму
    static final String ADD_RATING_FILM = """
            MERGE INTO rating_movies (film_id, user_id, rating)\s
            KEY(film_id, user_id)\s
            VALUES (?, ?, ?);""";

    //SQL-запрос для обновления рейтинга фильма в таблице films
    static final String UPDATE_RATING_FILM = """
            MERGE INTO films (id, rating)\s
            KEY(id)\s
            VALUES (?, ?);""";


    //SQL-запрос для удаления лайка с фильма
    static final String DELETE_LIKE = "DELETE FROM rating_movies WHERE film_id = ? AND user_id = ?";
    //SQL-запрос для получения count популярных фильмов по указанным жанру и году
    public static final String FIND_POPULAR_FILMS_BY_GENRE_ID_BY_YEAR = """
            SELECT f.*
            FROM films f
            LEFT JOIN movie_genres mg ON f.id = mg.film_id
            LEFT JOIN rating_movies lm ON f.id = lm.film_id
            WHERE (? IS NULL OR mg.genre_id = ?)
              AND (? IS NULL OR EXTRACT(YEAR FROM f.release_date) = ?)
            GROUP BY f.id
            ORDER BY AVG(lm.rating) DESC, COUNT(lm.user_id) DESC, f.id ASC
            LIMIT ?
            """;

//    static final String FIND_POPULAR_FILMS_BY_GENRE_ID_BY_YEAR = """
//            SELECT f.*
//            FROM films f
//            LEFT JOIN movie_genres mg ON f.id = mg.film_id
//            LEFT JOIN rating_movies lm ON f.id = lm.film_id
//            WHERE (? IS NULL OR mg.genre_id = ?)
//            AND (? IS NULL OR EXTRACT(YEAR FROM f.release_date) = ?)
//            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id
//            ORDER BY COUNT(lm.user_id) DESC
//            LIMIT ?
//            """;

    //SQL-запрос для проверки существования рейтинга MPA
    static final String CHECK_MPA_ID = "SELECT COUNT(*) FROM mpa_rating WHERE id = ?";

    //SQL-запрос для проверки существования жанра
    static final String CHEK_GENRE_ID = "SELECT COUNT(*) FROM genres WHERE id = ?";

    //SQL-запрос для получения всех рейтингов MPA
    static final String FIND_ALL_MPA = "SELECT * FROM mpa_rating ORDER BY id ASC";

    //SQL-запрос для поиска рейтинга MPA по id
    static final String FIND_BY_ID_MPA = "SELECT * FROM mpa_rating WHERE id = ?";

    //SQL-запрос для получения всех жанров
    static final String FIND_ALL_GENRE = "SELECT * FROM genres";

    //SQL-запрос для поиска жанра по id
    static final String FIND_BY_ID_GENRE = "SELECT * FROM genres WHERE id = ?";

    //SQL-запрос для поиска общих фильмов
    static final String FIND_BY_COMMON_FILMS = """
            SELECT f.*
            FROM rating_movies lm1
            JOIN rating_movies AS lm2 ON lm1.film_id = lm2.film_id
            JOIN films AS f ON f.id = lm1.film_id
            WHERE lm1.user_id = ? AND lm2.user_id = ?
            GROUP BY f.id
            ORDER BY COUNT(lm1.film_id) DESC;
            """;

    //SQL-запрос для добавления события пользователя
    static final String INSERT_USER_EVENT = """
            INSERT INTO EVENTS (timestamp, user_id, event_type, operation, entity_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    //SQL-запрос для поиска фильмов по названию и
    static final String FIND_FILMS_BY_TITLE_BY_DIRECTOR = """
            SELECT
                f.id,
                f.name,
                f.description,
                f.release_date,
                f.duration,
                f.mpa_rating_id,
                COUNT(lm.user_id) AS likes_count
            FROM films f
            LEFT JOIN film_directors fd ON f.id = fd.film_id
            LEFT JOIN directors d ON fd.director_id = d.id
            LEFT JOIN rating_movies lm ON f.id = lm.film_id
            WHERE ? AND LOWER(f.name) LIKE '%' || LOWER(?) || '%'
                OR ? AND LOWER(d.name) LIKE '%' || LOWER(?) || '%'
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id
            ORDER BY likes_count DESC""";
}
