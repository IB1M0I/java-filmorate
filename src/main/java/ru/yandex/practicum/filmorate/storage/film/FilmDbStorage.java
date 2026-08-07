package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static ru.yandex.practicum.filmorate.storage.film.FilmDirectorSql.*;
import static ru.yandex.practicum.filmorate.storage.film.FilmSql.*;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage, FilmDirectorStorage {
    private final RowMapper<Film> rowMapper;
    private final JdbcTemplate jdbc;

    //Добавить фильм в базу данных
    @Override
    public Film addFilm(Film film) {
        log.debug("Добавление фильма: {}", film.getName());
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата выпуска фильма не может быть раньше 28 декабря 1895 года");
        }

        if (film.getMpa() != null) {
            checkMpa(film.getMpa().getId());
        } else {
            throw new ValidationException("Рейтинг MPA не может быть пустым");
        }


        if (film.getGenres() != null) {
            film.getGenres().stream()
                    .map(Genre::getId)
                    .forEach(id -> checkGenre(id));
        } else {
            film.setGenres(new LinkedHashSet<>());
        }

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            validateDirectors(film.getDirectors());  // Вызов нового приватного метода
        }

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_FILM, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, film.getName());
            ps.setObject(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setObject(4, film.getDuration());
            ps.setObject(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            film.setId(id);
            log.info("Фильм успешно добавлен с id: {}", id);
            if (film.getGenres() != null) {
                insertGenresBatch(id, film.getGenres());
            } else {
                film.setGenres(new LinkedHashSet<>());
            }
            if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
                insertDirectorsBatch(film.getId(), film.getDirectors());
            }
        } else {
            log.error("Не удалось сохранить фильм и получить id");
            throw new RuntimeException("Не удалось сохранить фильм и получить id");
        }
        return findById(film.getId());

    }


    //Обновить информацию о фильме в базе данных
    @Override
    public Film updateFilm(Film film) {
        log.debug("Обновление фильма с id: {}", film.getId());
        Film existingFilm = findById(film.getId()); // Если фильма нет - выбросит NotFoundException

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата выпуска фильма не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            validateDirectors(film.getDirectors());
        }

        // Удаляем старые связи
        jdbc.update("DELETE FROM movie_genres WHERE film_id = ?", film.getId());
        jdbc.update("DELETE FROM film_directors WHERE film_id = ?", film.getId());

        // Обновляем фильм
        int updatedRows = jdbc.update(UPDATE_FILM, film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        // Проверяем, что обновление произошло
        if (updatedRows == 0) {
            log.error("Фильм с id={} не найден", film.getId());
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }
        log.info("Фильм с id {} успешно обновлен", film.getId());

        insertGenresBatch(film.getId(), film.getGenres());

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            insertDirectorsBatch(film.getId(), film.getDirectors());
        }

        return findById(film.getId());
    }


    //Найти фильм по id
    @Override
    public Film findById(long id) {
        log.debug("Поиск фильма с id: {}", id);
        try {
            Film film = jdbc.queryForObject(FIND_FILM_BY_ID, rowMapper, id);
            getLikesAndGenresByFilmId(List.of(film));
            return film;
        } catch (EmptyResultDataAccessException e) {
            log.error("Фильм с id {} не найден", id);
            throw new NotFoundException("Фильм не найден");
        }
    }

    //Получить список всех фильмов
    @Override
    public Collection<Film> findAll() {
        log.debug("Получение всех фильмов");
        Collection<Film> films = getLikesAndGenresByFilmId(jdbc.query(FIND_ALL_FILMS, rowMapper));
        log.info("Получено {} фильмов", films.size());
        return films;
    }

    //Добавить лайк фильму
    public Film likeFilm(long id, long userId) {
        log.debug("Пользователь {} ставит лайк фильму {}", userId, id);
        Film film = findById(id);
        int row = jdbc.update(LIKE_FILM, id, userId);

        if (row > 0) {
            addEvent(Instant.now().toEpochMilli(), userId, EventType.LIKE, Operation.ADD, id);
            log.info("Лайк успешно добавлен: пользователь {} фильму {}", userId, id);
            return film;
        } else {
            log.error("Не удалось добавить лайк");
            throw new RuntimeException("Не удалось добавить лайк");
        }
    }

    //Удалить лайк с фильма
    public int deleteLike(long id, long userId) {
        log.debug("Пользователь {} удаляет лайк с фильма {}", userId, id);
        int row = jdbc.update(DELETE_LIKE, id, userId);
        if (row > 0) {
            addEvent(Instant.now().toEpochMilli(), userId, EventType.LIKE, Operation.REMOVE, id);
            log.info("Лайк успешно удален: пользователь {} с фильма {}", userId, id);
            return row;
        } else {
            log.error("Не удалось удалить лайк");
            throw new RuntimeException("Ну удалось удалить лайк");
        }
    }

    //Получить count популярных фильмов по указанным жанру и году
    public Collection<Film> getPopularFilmsByGenreIdByYear(int count, Integer genreId, Integer year) {
        log.debug("Получение {} популярных фильмов, жанр: {}, год: {}", count, genreId, year);
        List<Film> popularFilm = jdbc.query(FIND_POPULAR_FILMS_BY_GENRE_ID_BY_YEAR, rowMapper, genreId, genreId, year, year, count);

        getLikesAndGenresByFilmId(popularFilm);
        log.info("Получено {} популярных фильмов", popularFilm.size());
        return popularFilm;
    }

    // Проверяем существование режиссера
    public Collection<Film> getFilmsByDirectorSorted(long directorId, String sortBy) {
        log.debug("Получение фильмов режиссера с id: {}, сортировка: {}", directorId, sortBy);
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM directors WHERE id = ?",
                Integer.class,
                directorId
        );
        if (count == null || count == 0) {
            throw new NotFoundException("Режиссер с id = " + directorId + " не найден");
        }

        String sql;
        if ("year".equals(sortBy)) {
            sql = FIND_FILMS_BY_DIRECTOR_SORT_BY_YEAR;
        } else if ("likes".equals(sortBy)) {
            sql = FIND_FILMS_BY_DIRECTOR_SORT_BY_LIKES;
        } else {
            throw new ValidationException("Неверный параметр сортировки: " + sortBy);
        }

        List<Film> films = jdbc.query(sql, rowMapper, directorId);
        log.info("Получено {} фильмов режиссера с id: {}", films.size(), directorId);
        return getLikesAndGenresByFilmId(films);
    }

    //Проверить существование рейтинга MPA
    public void checkMpa(int mpaId) {
        Integer count = jdbc.queryForObject(CHECK_MPA_ID, Integer.class, mpaId);

        if (count == null || count == 0) {
            throw new NotFoundException("MPA не найден");
        }
    }

    //Проверить существование жанра
    public void checkGenre(int genreId) {
        Integer count = jdbc.queryForObject(CHEK_GENRE_ID, Integer.class, genreId);

        if (count == null || count == 0) {
            throw new NotFoundException("Жанр не найден");
        }
    }


    //Загрузить лайки и жанры для списка фильмов
    public Collection<Film> getLikesAndGenresByFilmId(Collection<Film> films) {
        if (films == null || films.isEmpty()) {
            return films;
        }

        Map<Long, Film> filmMap = new LinkedHashMap<>();
        for (Film film : films) {
            filmMap.put(film.getId(), film);
            if (film.getGenres() == null) {
                film.setGenres(new LinkedHashSet<>());
            }
            if (film.getLikes() == null) {
                film.setLikes(new HashSet<>());
            }
        }

        String inClause = String.join(",", Collections.nCopies(films.size(), "?"));
        Object[] filmIds = films.stream().map(Film::getId).toArray();

        String getGenresByFilmId = "SELECT fg.film_id, g.id AS genre_id, g.name AS genre_name " +
                "FROM movie_genres fg " +
                "JOIN genres g ON fg.genre_id = g.id " +
                "WHERE fg.film_id IN (" + inClause + ") " +
                "ORDER BY fg.film_id, g.id";


        jdbc.query(getGenresByFilmId, (rs) -> {
            long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);

            if (film != null) {
                int genreId = rs.getInt("genre_id");
                if (genreId > 0) {
                    Genre genre = new Genre(genreId, rs.getString("genre_name"));
                    film.getGenres().add(genre);
                }
            }
        }, filmIds);

        String getLike = "SELECT film_id, user_id FROM likes_movies " +
                "WHERE film_id IN (" + inClause + ")";

        jdbc.query(getLike, (rs) -> {
            long filmId = rs.getLong("film_id");
            long userId = rs.getLong("user_id");

            Film film = filmMap.get(filmId);
            if (film != null) {
                film.getLikes().add(userId);
            }
        }, filmIds);

        String getMpa = "SELECT f.id AS film_id, m.id AS mpa_id, m.name AS mpa_name " +
                "FROM films f " +
                "JOIN mpa_rating m ON f.mpa_rating_id = m.id " +
                "WHERE f.id IN (" + inClause + ")";

        jdbc.query(getMpa, (rs) -> {
            long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);
            if (film != null) {
                film.setMpa(new MpaRating(
                        rs.getInt("mpa_id"),
                        rs.getString("mpa_name")  // ← name загружается!
                ));
            }
        }, filmIds);

        String getDirectors = FIND_DIRECTORS_BY_FILM_IDS + inClause + ")";
        jdbc.query(getDirectors, (rs) -> {
            long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);
            if (film != null) {
                Director director = new Director(
                        rs.getLong("director_id"),
                        rs.getString("director_name")
                );
                film.getDirectors().add(director);
            }
        }, filmIds);

        System.out.println("После загрузки:");
        for (Film film : filmMap.values()) {
            System.out.println("Film id = " + film.getId());
            System.out.println("MPA = " + film.getMpa());
            System.out.println("Genres = " + film.getGenres());
        }

        return filmMap.values();
    }

    //Получить список всех рейтингов MPA
    public Collection<MpaRating> findAllMpa() {
        return jdbc.query(FIND_ALL_MPA, (rs, rowMapper) -> new MpaRating(rs.getInt("id"), rs.getString("name")));
    }

    //Найти рейтинг MPA по id
    public MpaRating findByIdMpa(int id) {
        try {
            return jdbc.queryForObject(FIND_BY_ID_MPA, (rs, rowMapper) -> new MpaRating(rs.getInt("id"), rs.getString("name")), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("MPA не найден");
        }
    }

    //Получить список всех жанров
    public Collection<Genre> findAllGenres() {
        return jdbc.query(FIND_ALL_GENRE, (rs, rowMapper) -> new Genre(rs.getInt("id"), rs.getString("name")));
    }

    //Найти жанр по id
    public Genre findGenreById(long id) {
        try {
            return jdbc.queryForObject(FIND_BY_ID_GENRE, (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанр не найден");
        }
    }

    //Массовая вставка жанров для фильма
    public void insertGenresBatch(long filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }

        List<Genre> listGenre = new ArrayList<>(genres);

        jdbc.batchUpdate(INSERT_GENRE_TO_FILM, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, listGenre.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return listGenre.size();
            }
        });

    }

    //Добавить событие
    @Override
    public void addEvent(long timestamp, long userId, EventType eventType, Operation operation, long entityId) {
        jdbc.update(INSERT_USER_EVENT, timestamp, userId, eventType.name(), operation.name(), entityId);
    }

    //Получить список общих фильмов двух пользователей
    public Collection<Film> getCommonFilms(long userId, long friendId) {
        log.debug("Получение общих фильмов пользователей {} и {}", userId, friendId);
        List<Film> films = jdbc.query(FIND_BY_COMMON_FILMS, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            return film;
        }, userId, friendId);
        getLikesAndGenresByFilmId(films);
        log.info("Получено {} общих фильмов", films.size());
        return films;
    }

    public void insertDirectorsBatch(long filmId, Set<Director> directors) {
        if (directors == null || directors.isEmpty()) {
            return;
        }

        List<Director> listDirector = new ArrayList<>(directors);

        jdbc.batchUpdate(INSERT_FILM_DIRECTOR, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, listDirector.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return listDirector.size();
            }
        });
    }

    private void validateDirectors(Set<Director> directors) {
        for (Director director : directors) {
            Integer count = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM directors WHERE id = ?",
                    Integer.class,
                    director.getId()
            );
            if (count == null || count == 0) {
                throw new NotFoundException("Режиссёр с id = " + director.getId() + " не найден");
            }
        }
    }

    public Collection<Film> searchFilmsByTitleByDirector(String query, boolean title, boolean director) {
        log.debug("Поиск фильмов по названию: {}, по режиссеру: {}", title, director);
        Collection<Film> films = getLikesAndGenresByFilmId(
                jdbc.query(FIND_FILMS_BY_TITLE_BY_DIRECTOR, rowMapper, title, query, director, query)
        );
        log.info("Получено {} фильмов по запросу: {}", films.size(), query);
        return films;
    }

    @Override
    public void deleteFilm(long id) {
        log.debug("Удаление фильма с id: {}", id);
        findById(id);

        //Сначала удаляем связи с жанрами
        jdbc.update("DELETE FROM movie_genres WHERE film_id = ?", id);

        //Удаляем связи с режиссерами
        jdbc.update("DELETE FROM film_directors WHERE film_id = ?", id);

        //Удаляем лайки
        jdbc.update("DELETE FROM likes_movies WHERE film_id = ?", id);

        //Удаляем отзывы (если есть)
        jdbc.update("DELETE FROM reviews WHERE film_id = ?", id);

        //удаляем сам фильм
        int rowsDeleted = jdbc.update("DELETE FROM films WHERE id = ?", id);
        if (rowsDeleted == 0) {
            log.error("Фильм с id = {} не найден", id);
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        log.info("Фильм с id {} успешно удален", id);
    }
}
