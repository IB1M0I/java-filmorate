package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;

import static ru.yandex.practicum.filmorate.storage.director.DirectorSql.INSERT_DIRECTOR;
import static ru.yandex.practicum.filmorate.storage.director.DirectorSql.UPDATE_DIRECTOR;
import static ru.yandex.practicum.filmorate.storage.director.DirectorSql.FIND_ALL_DIRECTORS;
import static ru.yandex.practicum.filmorate.storage.director.DirectorSql.FIND_DIRECTOR_BY_ID;
import static ru.yandex.practicum.filmorate.storage.director.DirectorSql.DELETE_DIRECTOR;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<Director> rowMapper;

    @Override
    public Director create(Director director) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_DIRECTOR, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            director.setId(id);
        } else {
            throw new RuntimeException("Не удалось сохранить режиссера и получить id");
        }
        return director;
    }

    @Override
    public Director update(Director director) {
        int rowsUpdated = jdbc.update(UPDATE_DIRECTOR, director.getName(), director.getId());
        if (rowsUpdated == 0) {
            throw new NotFoundException("Режиссер с id = " + director.getId() + " не найден");
        }
        return director;
    }

    @Override
    public Director findById(long id) {
        try {
            return jdbc.queryForObject(FIND_DIRECTOR_BY_ID, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Режиссер с id = " + id + " не найден");
        }
    }

    @Override
    public Collection<Director> findAll() {
        return jdbc.query(FIND_ALL_DIRECTORS, rowMapper);
    }

    @Override
    public void delete(long id) {
        int rowsDeleted = jdbc.update(DELETE_DIRECTOR, id);
        if (rowsDeleted == 0) {
            throw new NotFoundException("Режиссер с id = " + id + " не найден");
        }
    }
}