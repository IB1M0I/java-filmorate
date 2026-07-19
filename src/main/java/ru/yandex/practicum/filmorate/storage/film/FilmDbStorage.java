package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

import static ru.yandex.practicum.filmorate.storage.film.FilmSql.*;

@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final RowMapper<Film> rowMapper;
    private final JdbcTemplate jdbc;


    @Override
    public Film addFilm(Film film) {
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
            if (film.getGenres() != null) {
                insertGenresBatch(id, film.getGenres());
            } else {
                film.setGenres(new LinkedHashSet<>());
            }
        } else {
            throw new RuntimeException("Не удалось сохранить фильм и получить id");
        }
        return film;

    }


    @Override
    public Film updateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата выпуска фильма не может быть раньше 28 декабря 1895 года");
        }

        jdbc.update("DELETE FROM movie_genres WHERE film_id = ?", film.getId());
        jdbc.update(UPDATE_FILM, film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        insertGenresBatch(film.getId(), film.getGenres());

        return film;
    }

    @Override
    public void deleteFilm(long id) {
        jdbc.update(DELETE_FILM, id);
    }

    @Override
    public Film findById(long id) {
        try {
            Film film = jdbc.queryForObject(FIND_FILM_BY_ID, rowMapper, id);
            getLikesAndGenresByFilmId(List.of(film));
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм не найден");
        }
    }

    @Override
    public Collection<Film> findAll() {
        return getLikesAndGenresByFilmId(jdbc.query(FIND_ALL_FILMS, rowMapper));
    }

    public Film likeFilm(long id, long userId) {
        Film film = findById(id);
        jdbc.update(LIKE_FILM, id, userId);
        return film;
    }

    public int deleteLike(long id, long userId) {
        return jdbc.update(DELETE_LIKE, id, userId);
    }

    public Collection<Film> getPopular(int count) {
        List<Film> popularFilm = jdbc.query(FIND_POPULAR, rowMapper, count);

        getLikesAndGenresByFilmId(popularFilm);
        return popularFilm;
    }

    public void checkMpa(int mpaId) {
        Integer count = jdbc.queryForObject(CHECK_MPA_ID, Integer.class, mpaId);

        if (count == null || count == 0) {
            throw new NotFoundException("MPA не найден");
        }
    }

    public void checkGenre(int genreId) {
        Integer count = jdbc.queryForObject(CHEK_GENRE_ID, Integer.class, genreId);

        if (count == null || count == 0) {
            throw new NotFoundException("Жанр не найден");
        }
    }


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

        jdbc.query(get_like, (rs) -> {
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

        jdbc.query(get_mpa, (rs) -> {
            long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);
            if (film != null) {
                film.setMpa(new MpaRating(
                        rs.getInt("mpa_id"),
                        rs.getString("mpa_name")  // ← name загружается!
                ));
            }
        }, filmIds);

        return filmMap.values();
    }

    public Collection<MpaRating> findAllMpa() {
        return jdbc.query(FIND_ALL_MPA, (rs, rowMapper) -> new MpaRating(rs.getInt("id"), rs.getString("name")));
    }

    public MpaRating findByIdMpa(int id) {
        try {
            return jdbc.queryForObject(FIND_BY_ID_MPA, (rs, rowMapper) -> new MpaRating(rs.getInt("id"), rs.getString("name")), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("MPA не найден");
        }
    }

    public Collection<Genre> findAllGenres() {
        return jdbc.query(FIND_ALL_GENRE, (rs, rowMapper) -> new Genre(rs.getInt("id"), rs.getString("name")));
    }

    public Genre findGenreById(long id) {
        try {
            return jdbc.queryForObject(FIND_BY_ID_GENRE, (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанр не найден");
        }
    }

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
}
