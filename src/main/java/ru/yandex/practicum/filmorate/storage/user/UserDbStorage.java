package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;

import static ru.yandex.practicum.filmorate.storage.user.UserSql.*;

@RequiredArgsConstructor
@Repository
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<User> rowMapper;



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


    @Override
    public User updateUser(User user) {
        jdbc.update(UserSql.UPDATE_USER,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User deleteUser(long id) {
        User user = findById(id);
        jdbc.update(DELETE_USER, id);
        return user;
    }

    @Override
    public User findById(long id) {
        try {
            return jdbc.queryForObject(FIND_USER_BY_ID, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    @Override
    public Collection<User> findAll() {
        return jdbc.query(FIND_ALL_USERS, rowMapper);
    }

    public Collection<User> getFriends(long id) {
        return jdbc.query(FIND_ALL_FRIENDS, rowMapper, id);
    }

    public User addFriend(long id, long friendId, boolean isConfirmed) {
        User user = findById(friendId);
        jdbc.update(ADD_FRIEND, id, friendId, isConfirmed);
        return user;
    }

    public void updateFriendshipIsConfirmed(long id, long friendId, boolean isConfirmed) {
        jdbc.update(UPDATE_FRIENDSHIPS_IS_CONFIRMED, isConfirmed, id, friendId);
    }

    public void deleteFriend(long id, long friendId) {
        jdbc.update(DELETE_FRIEND, id, friendId);
    }

    public Collection<User> getCommonFriends(long id, long otherId) {
        return jdbc.query(COMMON_FRIEND, rowMapper, id, otherId);
    }
}
