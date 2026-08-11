package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.EventRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.Collection;

import static ru.yandex.practicum.filmorate.storage.user.UserSql.*;


@Repository
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<User> rowMapper;
    private final FilmDbStorage filmStorage;
    private final RowMapper<Film> filmRowMapper;

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> rowMapper, @Lazy FilmDbStorage filmStorage, RowMapper<Film> filmRowMapper) {
        this.jdbc = jdbc;
        this.rowMapper = rowMapper;
        this.filmStorage = filmStorage;
        this.filmRowMapper = filmRowMapper;
    }


    //Добавить пользователя в базу данных
    @Override
    public User addUser(User user) {
        log.debug("Добавление пользователя: {}", user.getLogin());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_USER, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setObject(1, user.getEmail());
            ps.setObject(2, user.getLogin());
            ps.setObject(3, user.getName());
            ps.setObject(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            user.setId(id);
            log.info("Пользователь успешно добавлен с id: {}", id);
        } else {
            log.error("Не удалось сохранить пользователя и получить id");
            throw new RuntimeException("Не удалось сохранить пользователя и получить id");
        }
        return user;
    }


    //Обновить данные пользователя в базе данных
    @Override
    public User updateUser(User user) {
        log.debug("Обновление пользователя с id: {}", user.getId());
        jdbc.update(UserSql.UPDATE_USER,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        log.info("Пользователь с id {} успешно обновлен", user.getId());
        return user;
    }


    //Найти пользователя по id
    @Override
    public User findById(long id) {
        try {
            return jdbc.queryForObject(FIND_USER_BY_ID, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Пользователь с id " + id + " не найден");
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    //Получить список всех пользователей
    @Override
    public Collection<User> findAll() {
        log.debug("Получение всех пользователей");
        Collection<User> users = jdbc.query(FIND_ALL_USERS, rowMapper);
        log.info("Получено {} пользователей", users.size());
        return users;
    }

    //Получить список друзей пользователя
    public Collection<User> getFriends(long id) {
        log.debug("Получение друзей пользователя с id: {}", id);
        Collection<User> friends = jdbc.query(FIND_ALL_FRIENDS, rowMapper, id);
        log.info("Получено {} друзей пользователя с id: {}", friends.size(), id);
        return friends;
    }

    //Добавить друга
    public User addFriend(long id, long friendId, boolean isConfirmed) {
        log.debug("Добавление друга: пользователь {} добавляет пользователя {}", id, friendId);
        User user = findById(friendId);

        // Проверяем, есть ли уже встречная заявка (friendId уже добавил id раньше)
        boolean reverseExists = friendshipExists(friendId, id);
        boolean confirmed = isConfirmed || reverseExists;

        int row = jdbc.update(ADD_FRIEND, id, friendId, confirmed);

        if (row > 0) {
            if (reverseExists) {
                // Обновляем встречную запись на подтверждённую
                updateFriendshipIsConfirmed(friendId, id, true);
            }
            addEvent(Instant.now().toEpochMilli(), id, EventType.FRIEND, Operation.ADD, friendId);
            log.info("Друг успешно добавлен: пользователь {} добавил пользователя {}", id, friendId);
            return user;
        } else {
            log.error("Не удалось добавить друга");
            throw new RuntimeException("Не удалось добавить друга");
        }
    }

    //Проверить существование записи о дружбе
    private boolean friendshipExists(long id, long friendId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?",
                Integer.class, id, friendId);
        return count != null && count > 0;
    }



    //Обновить статус подтверждения дружбы
    public void updateFriendshipIsConfirmed(long id, long friendId, boolean isConfirmed) {
        log.debug("Обновление статуса дружбы между пользователями {} и {} на: {}", id, friendId, isConfirmed);
        jdbc.update(UPDATE_FRIENDSHIPS_IS_CONFIRMED, isConfirmed, id, friendId);
        log.info("Статус дружбы успешно обновлен");
    }

    //Удалить друга
    public void deleteFriend(long id, long friendId) {
        log.debug("Удаление друга: пользователь {} удаляет пользователя {}", id, friendId);
        int row = jdbc.update(DELETE_FRIEND, id, friendId);

        if (row > 0) {
            // Если была взаимная (подтверждённая) дружба — откатываем обратную запись до неподтверждённой,
            // а не удаляем её полностью (пользователь friendId по-прежнему может считать id своим "запрошенным" другом)
            boolean reverseExists = friendshipExists(friendId, id);
            if (reverseExists) {
                updateFriendshipIsConfirmed(friendId, id, false);
            }
            addEvent(Instant.now().toEpochMilli(), id, EventType.FRIEND, Operation.REMOVE, friendId);
            log.info("Друг успешно удален: пользователь {} удалил пользователя {}", id, friendId);
        }
    }


    //Получить список общих друзей двух пользователей
    public Collection<User> getCommonFriends(long id, long otherId) {
        log.debug("Получение общих друзей пользователей {} и {}", id, otherId);
        Collection<User> commonFriends = jdbc.query(COMMON_FRIEND, rowMapper, id, otherId);
        log.info("Получено {} общих друзей", commonFriends.size());
        return commonFriends;
    }

    public Collection<Film> getRecommendations(long id) {
        log.debug("Получение рекомендаций для пользователя с id: {}", id);
        Collection<Film> films =  jdbc.query(GET_RECOMMENDATIONS, filmRowMapper, id, id);
        filmStorage.getLikesAndGenresByFilmId(films);
        log.info("Получено {} рекомендованных фильмов", films.size());
        return films;
    }

    //Получить события пользователя
    public Collection<Event> getEventsUser(long id) {
        log.debug("Получение событий пользователя с id: {}", id);
        findById(id);
        Collection<Event> events = jdbc.query(FIND_USER_EVENT_ID, new EventRowMapper(), id);
        log.info("Получено {} событий пользователя с id: {}", events.size(), id);
        return events;
    }

    //Добавить событие
    @Override
    public void addEvent(long timestamp, long userId, EventType eventType, Operation operation, long entityId) {
        log.debug("Добавление события: пользователь {}, тип {}, операция {}, сущность {}", userId, eventType, operation, entityId);
        jdbc.update(INSERT_USER_EVENT, timestamp, userId, eventType.name(), operation.name(), entityId);
    }

    @Override
    public void deleteUser(long id) {
        log.debug("Удаление пользователя с id: {}", id);
        findById(id);
        int rowsDeleted = jdbc.update("DELETE FROM users WHERE id = ?", id);
        if (rowsDeleted == 0) {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        log.info("Пользователь с id {} успешно удален", id);
    }
}
