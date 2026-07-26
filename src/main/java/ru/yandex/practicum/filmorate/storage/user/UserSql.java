package ru.yandex.practicum.filmorate.storage.user;

public class UserSql {
    private UserSql() {
    }

    //SQL-запрос для добавления пользователя
    static final String INSERT_USER = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    //SQL-запрос для обновления пользователя
    static final String UPDATE_USER = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    //SQL-запрос для поиска пользователя по id
    static final String FIND_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    //SQL-запрос для получения всех пользователей
    static final String FIND_ALL_USERS = "SELECT * FROM users";

    //SQL-запрос для получения всех друзей пользователя
    static final String FIND_ALL_FRIENDS = """
            SELECT u.*
            FROM users u
            JOIN friendships f ON u.id = f.friend_id
            WHERE f.user_id = ?;""";
    //SQL-запрос для добавления друга
    static final String ADD_FRIEND = "INSERT INTO friendships (user_id, friend_id, is_confirmed) VALUES (?, ?, ?)";
    //SQL-запрос для обновления статуса подтверждения дружбы
    static final String UPDATE_FRIENDSHIPS_IS_CONFIRMED = "UPDATE friendships SET is_confirmed = ? WHERE user_id = ? AND friend_id = ?";
    //SQL-запрос для удаления друга
    static final String DELETE_FRIEND = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
    //SQL-запрос для получения общих друзей двух пользователей
    static final String COMMON_FRIEND = """
            SELECT u.*
            FROM users AS u
            WHERE u.id IN (
            \tSELECT friend_id FROM friendships WHERE user_id = ?
            ) AND u.id IN (
            \tSELECT friend_id FROM friendships WHERE user_id = ?
            )""";
}
