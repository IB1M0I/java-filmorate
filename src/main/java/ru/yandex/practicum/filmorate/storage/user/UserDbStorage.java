package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@RequiredArgsConstructor
@Repository
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<User> rowMapper;
    private final FilmDbStorage filmStorage;
    private final RowMapper<Film> filmRowMapper;


    //Добавить пользователя в базу данных
    @Override
    public User addUser(User user) {
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
        } else {
            throw new RuntimeException("Не удалось сохранить пользователя и получить id");
        }
        return user;
    }


    //Обновить данные пользователя в базе данных
    @Override
    public User updateUser(User user) {
        jdbc.update(UserSql.UPDATE_USER,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
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
        return jdbc.query(FIND_ALL_USERS, rowMapper);
    }

    //Получить список друзей пользователя
    public Collection<User> getFriends(long id) {
        return jdbc.query(FIND_ALL_FRIENDS, rowMapper, id);
    }

    //Добавить друга
    public User addFriend(long id, long friendId, boolean isConfirmed) {
        User user = findById(friendId);
        int row = jdbc.update(ADD_FRIEND, id, friendId, isConfirmed);

        if (row > 0) {
            addEvent(Instant.now().toEpochMilli(), id, EventType.FRIEND, Operation.ADD, friendId);
            return user;
        } else {
            throw new RuntimeException("Не удалось добавить друга");
        }

    }

    //Обновить статус подтверждения дружбы
    public void updateFriendshipIsConfirmed(long id, long friendId, boolean isConfirmed) {
        jdbc.update(UPDATE_FRIENDSHIPS_IS_CONFIRMED, isConfirmed, id, friendId);
    }

    //Удалить друга
    public void deleteFriend(long id, long friendId) {
        int row = jdbc.update(DELETE_FRIEND, id, friendId);

        if (row > 0) {
            addEvent(Instant.now().toEpochMilli(), id, EventType.FRIEND, Operation.REMOVE, friendId);
        }
    }


    //Получить список общих друзей двух пользователей
    public Collection<User> getCommonFriends(long id, long otherId) {
        return jdbc.query(COMMON_FRIEND, rowMapper, id, otherId);
    }

    public Collection<Film> getRecommendations(long id) {
        Collection<Film> films =  jdbc.query(GET_RECOMMENDATIONS, filmRowMapper, id, id);
        filmStorage.getLikesAndGenresByFilmId(films);
        return films;
    }

    //Получить события пользователя
    public Collection<Event> getEventsUser(long id) {
        findById(id);
        return jdbc.query(FIND_USER_EVENT_ID, new EventRowMapper(), id);
    }

    //Добавить событие
    @Override
    public void addEvent(long timestamp, long userId, EventType eventType, Operation operation, long entityId) {
        jdbc.update(INSERT_USER_EVENT, timestamp, userId, eventType.name(), operation.name(), entityId);
    }

    @Override
    public void deleteUser(long id) {
        findById(id);
        int rowsDeleted = jdbc.update("DELETE FROM users WHERE id = ?", id);
        if (rowsDeleted == 0) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }
}
