package ru.yandex.practicum.filmorate.storage.director;

public class DirectorSql {
    private DirectorSql() {
    }

    //SQL-запрос для создания режиссера
    public static final String INSERT_DIRECTOR =
            "INSERT INTO directors (name) VALUES (?)";

    //SQL-запрос для обновления режиссера
    public static final String UPDATE_DIRECTOR =
            "UPDATE directors SET name = ? WHERE id = ?";

    //SQL-запрос для получения всех режиссеров
    public static final String FIND_ALL_DIRECTORS =
            "SELECT * FROM directors";

    //SQL-запрос для поиска режиссера по id
    public static final String FIND_DIRECTOR_BY_ID =
            "SELECT * FROM directors WHERE id = ?";

    //SQL-запрос для удаления режиссера
    public static final String DELETE_DIRECTOR =
            "DELETE FROM directors WHERE id = ?";

    //SQL-запрос для проверки существования режиссера
    public static final String CHECK_DIRECTOR_ID =
            "SELECT COUNT(*) FROM directors WHERE id = ?";
}