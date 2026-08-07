package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<Director> rowMapper;

    @Override
    //Создать режиссера в базе данных
    public Director create(Director director) {
        log.debug("Создание режиссера: {}", director.getName());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_DIRECTOR, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            director.setId(id);
            log.info("Режиссер успешно создан с id: {}", id);
        } else {
            log.error("Не удалось сохранить режиссера и получить id");
            throw new RuntimeException("Не удалось сохранить режиссера и получить id");
        }
        return director;
    }

    @Override
    //Обновить режиссера в базе данных
    public Director update(Director director) {
        log.debug("Обновление режиссера с id: {}", director.getId());
        int rowsUpdated = jdbc.update(UPDATE_DIRECTOR, director.getName(), director.getId());
        if (rowsUpdated == 0) {
            log.error("Режиссер с id = {} не найден", director.getId());
            throw new NotFoundException("Режиссер с id = " + director.getId() + " не найден");
        }
        log.info("Режиссер с id {} успешно обновлен", director.getId());
        return director;
    }

    @Override
    //Найти режиссера по id
    public Director findById(long id) {
        log.debug("Поиск режиссера с id: {}", id);
        try {
            return jdbc.queryForObject(FIND_DIRECTOR_BY_ID, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Режиссер с id = {} не найден", id);
            throw new NotFoundException("Режиссер с id = " + id + " не найден");
        }
    }

    @Override
    //Получить всех режиссеров
    public Collection<Director> findAll() {
        log.debug("Получение всех режиссеров");
        Collection<Director> directors = jdbc.query(FIND_ALL_DIRECTORS, rowMapper);
        log.info("Получено {} режиссеров", directors.size());
        return directors;
    }

    @Override
    //Удалить режиссера
    public void delete(long id) {
        log.debug("Удаление режиссера с id: {}", id);
        int rowsDeleted = jdbc.update(DELETE_DIRECTOR, id);
        if (rowsDeleted == 0) {
            log.error("Режиссер с id = {} не найден", id);
            throw new NotFoundException("Режиссер с id = " + id + " не найден");
        }
        log.info("Режиссер с id {} успешно удален", id);
    }
}