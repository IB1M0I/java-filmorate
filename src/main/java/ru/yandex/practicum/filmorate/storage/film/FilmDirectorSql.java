package ru.yandex.practicum.filmorate.storage.film;

public class FilmDirectorSql {
    private FilmDirectorSql() {
    }

    public static final String INSERT_FILM_DIRECTOR =
            "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";

    public static final String DELETE_FILM_DIRECTORS =
            "DELETE FROM film_directors WHERE film_id = ?";

    public static final String FIND_DIRECTORS_BY_FILM_IDS =
            "SELECT fd.film_id, d.id AS director_id, d.name AS director_name " +
                    "FROM film_directors fd " +
                    "JOIN directors d ON fd.director_id = d.id " +
                    "WHERE fd.film_id IN (";

    public static final String FIND_FILMS_BY_DIRECTOR_SORT_BY_YEAR =
            "SELECT f.* FROM films f " +
                    "JOIN film_directors fd ON f.id = fd.film_id " +
                    "WHERE fd.director_id = ? " +
                    "ORDER BY f.release_date ASC";

    public static final String FIND_FILMS_BY_DIRECTOR_SORT_BY_RATING =
            "SELECT f.*, COALESCE(AVG(rm.rating), 0) as avg_rating FROM films f " +
                    "JOIN film_directors fd ON f.id = fd.film_id " +
                    "LEFT JOIN rating_movies rm ON f.id = rm.film_id " +
                    "WHERE fd.director_id = ? " +
                    "GROUP BY f.id " +
                    "ORDER BY avg_rating DESC";

}
