package ru.yandex.practicum.filmorate.storage.film;

public class FilmSql {
    //SQL-запрос для добавления фильма
    static final String INSERT_FILM = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES (?,?,?,?,?)";
    //SQL-запрос для получения всех фильмов
    static final String FIND_ALL_FILMS = "SELECT * FROM films";
    //SQL-запрос для поиска фильма по id
    static final String FIND_FILM_BY_ID = "SELECT * FROM films WHERE id = ?";
    //SQL-запрос для обновления фильма
    static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE id = ?";
    //SQL-запрос для добавления жанра к фильму
    static final String INSERT_GENRE_TO_FILM = "MERGE INTO movie_genres (film_id, genre_id) KEY (film_id,genre_id) VALUES (?,?)";


    //SQL-запрос для добавления лайка фильму
    static final String LIKE_FILM = """
            MERGE INTO likes_movies (film_id, user_id)\s
            KEY(film_id, user_id)\s
            VALUES (?, ?);""";
    //SQL-запрос для удаления лайка с фильма
    static final String DELETE_LIKE = "DELETE FROM likes_movies WHERE film_id = ? AND user_id = ?";
    //SQL-запрос для получения популярных фильмов
    static final String FIND_POPULAR = """
            SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id
            FROM films AS f
            LEFT JOIN likes_movies AS lm ON f.id = lm.film_id
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id
            ORDER BY COUNT(lm.film_id) DESC, f.id ASC
            LIMIT ?""";

    //SQL-запрос для проверки существования рейтинга MPA
    static final String CHECK_MPA_ID = "SELECT COUNT(*) FROM mpa_rating WHERE id = ?";
    //SQL-запрос для проверки существования жанра
    static final String CHEK_GENRE_ID = "SELECT COUNT(*) FROM genres WHERE id = ?";

    //SQL-запрос для получения всех рейтингов MPA
    static final String FIND_ALL_MPA = "SELECT * FROM mpa_rating";
    //SQL-запрос для поиска рейтинга MPA по id
    static final String FIND_BY_ID_MPA = "SELECT * FROM mpa_rating WHERE id = ?";

    //SQL-запрос для получения всех жанров
    static final String FIND_ALL_GENRE = "SELECT * FROM genres";
    //SQL-запрос для поиска жанра по id
    static final String FIND_BY_ID_GENRE = "SELECT * FROM genres WHERE id = ?";

    //Новые SQL-запросы для работы с режиссерами
    static final String INSERT_FILM_DIRECTOR =
            "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";

    static final String DELETE_FILM_DIRECTORS =
            "DELETE FROM film_directors WHERE film_id = ?";

    static final String FIND_DIRECTORS_BY_FILM_IDS =
            "SELECT fd.film_id, d.id AS director_id, d.name AS director_name " +
                    "FROM film_directors fd " +
                    "JOIN directors d ON fd.director_id = d.id " +
                    "WHERE fd.film_id IN (";

    static final String FIND_FILMS_BY_DIRECTOR_SORT_BY_YEAR =
            "SELECT f.* FROM films f " +
                    "JOIN film_directors fd ON f.id = fd.film_id " +
                    "WHERE fd.director_id = ? " +
                    "ORDER BY f.release_date ASC";

    static final String FIND_FILMS_BY_DIRECTOR_SORT_BY_LIKES =
            "SELECT f.*, COUNT(lm.user_id) as like_count FROM films f " +
                    "JOIN film_directors fd ON f.id = fd.film_id " +
                    "LEFT JOIN likes_movies lm ON f.id = lm.film_id " +
                    "WHERE fd.director_id = ? " +
                    "GROUP BY f.id " +
                    "ORDER BY like_count DESC";
}
