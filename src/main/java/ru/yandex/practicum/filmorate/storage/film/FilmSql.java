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
    static final String FIND_ALL_MPA = "SELECT * FROM mpa_rating ORDER BY id ASC";
    //SQL-запрос для поиска рейтинга MPA по id
    static final String FIND_BY_ID_MPA = "SELECT * FROM mpa_rating WHERE id = ?";

    //SQL-запрос для получения всех жанров
    static final String FIND_ALL_GENRE = "SELECT * FROM genres";
    //SQL-запрос для поиска жанра по id
    static final String FIND_BY_ID_GENRE = "SELECT * FROM genres WHERE id = ?";
}
