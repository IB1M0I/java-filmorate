package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
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
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final RowMapper<Film> rowMapper;
    private final JdbcTemplate jdbc;
    private final String INSERT_FILM = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES (?,?,?,?,?)";
    private final String FIND_ALL_FILMS = "SELECT * FROM FILMS";
    private final String FIND_FILM_BY_ID = "SELECT * FROM films WHERE id = ?";
    private final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE id = ?";
    private final String DELETE_FILM = "DELETE FROM films WHERE id = ?";
    //TODO исправить movie_genre на genres, и genre на genres в shema.sql
    private final String INSERT_GENRE_TO_FILM = "MERGE INTO movie_genre (film_id, genre_id) KEY (film_id,genre_id) VALUES (?,?)";
    private final String GET_GENRE_BY_FILM_ID = "SELECT g.id, g.name\n" +
            "FROM movie_genre AS mg\n" +
            "JOIN genre AS g ON mg.genre_id = g.id\n" +
            "WHERE mg.film_id = ?";

    private final String LIKE_FILM = "MERGE INTO likes_movies (film_id, user_id) \n" +
            "KEY(film_id, user_id) \n" +
            "VALUES (?, ?);";
    private final String DELETE_LIKE = "DELETE FROM likes_movies WHERE film_id = ? AND user_id = ?";
    private final String FIND_POPULAR = "SELECT f.*\n" +
            "FROM films AS f\n" +
            "LEFT JOIN likes_movies AS lm ON f.id = lm.film_id\n" +
            "GROUP BY f.id\n" +
            "ORDER BY COUNT(lm.user_id) DESC\n" +
            "LIMIT ?";
    private final String GET_LIKES_BY_FILM_ID = "SELECT user_id FROM likes_movies WHERE film_id = ?";

    private final String CHECK_MPA_ID = "SELECT COUNT(*) FROM MPA_RATING WHERE id = ?";
    private final String CHEK_GENRE_ID = "SELECT COUNT(*) FROM GENRE WHERE id = ?";

    private final String FIND_ALL_MPA = "SELECT * FROM MPA_RATING";
    private final String FIND_BY_ID_MPA = "SELECT * FROM MPA_RATING WHERE id = ?";

    private final String FIND_ALL_GENRE = "SELECT * FROM GENRE";
    private final String FIND_BY_ID_GENRE = "SELECT * FROM GENRE WHERE id = ?";

    @Override
    public Film addFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата выпуска фильма не может быть раньше 28 декабря 1895 года");
        }

        if(film.getMpa() !=null){
            checkMpa(film.getMpa().getId());
        }else {
            throw new ValidationException("Рейтинг MPA не может быть пустым");
        }


        if(film.getGenre() != null){
            film.getGenre().stream()
                    .map(Genre::getId)
                    .forEach(id -> checkGenre(id));
        }else{
            throw new ValidationException("Жанры не могут быть пустыми");
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
            if (film.getGenre() != null) {
                for (Genre genre : film.getGenre()) {
                    jdbc.update(INSERT_GENRE_TO_FILM, film.getId(), genre.getId());
                }
            } else {
                film.setGenre(new LinkedHashSet<>());
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
        jdbc.update(UPDATE_FILM, film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        for (Genre genre : film.getGenre()) {
            jdbc.update(INSERT_GENRE_TO_FILM, film.getId(), genre.getId());
        }
        return film;
    }

    @Override
    public void deleteFilm(long id) {
        jdbc.update(DELETE_FILM, id);
    }

    @Override
    public Film findById(long id) {
        return jdbc.queryForObject(FIND_FILM_BY_ID, rowMapper, id);
    }

    @Override
    public Collection<Film> findAll() {
        return jdbc.query(FIND_ALL_FILMS, rowMapper);
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
        return jdbc.query(FIND_POPULAR, rowMapper, count);
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

    public Film getLikesAndGenresByFilmId(Film film) {
        Set<Long> likes = new HashSet<>(jdbc.query(GET_LIKES_BY_FILM_ID, (rs, rowMapper) -> rs.getLong("user_id"), film.getId()));
        LinkedHashSet<Genre> genres = new LinkedHashSet<>(jdbc.query(GET_GENRE_BY_FILM_ID, (rs, rowMapper) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, film.getId()));

        film.setLikes(likes);
        film.setGenre(genres);
        MpaRating mpa = jdbc.queryForObject(FIND_BY_ID_MPA,(rs, rowNum) -> new MpaRating(rs.getInt("id"),rs.getString("name")), film.getMpa().getId());
        film.setMpa(mpa);


        return film;
    }

    public Collection<Film> getLikesAndGenresByFilmId(Collection<Film> films) {
        for (Film film : films) {
            HashSet<Long> likes = new HashSet<>(jdbc.query(GET_LIKES_BY_FILM_ID, (rs, rowMapper) -> rs.getLong("user_id"), film.getId()));
            HashSet<Genre> genres = new HashSet<>(jdbc.query(GET_GENRE_BY_FILM_ID, (rs, rowMapper) -> {
                Genre genre = new Genre();
                genre.setId(rs.getInt("id"));
                return genre;
            }, film.getId()));
            film.setLikes(likes);
        }
        return films;
    }

    public Collection<MpaRating> findAllMpa() {
        return jdbc.query(FIND_ALL_MPA, (rs, rowMapper) -> new MpaRating(rs.getInt("id"), rs.getString("name")));
    }

    public MpaRating findByIdMpa(int id) {
        return jdbc.queryForObject(FIND_BY_ID_MPA, (rs, rowMapper) -> new MpaRating(rs.getInt("id"), rs.getString("name")), id);
    }

    public Collection<Genre> findAllGenres() {
        return jdbc.query(FIND_ALL_GENRE, (rs,rowMapper)-> new Genre(rs.getInt("id"),rs.getString("name")));
    }

    public Genre findGenreById(long id) {
        Genre genre = jdbc.queryForObject(FIND_BY_ID_GENRE, (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")), id);

        return genre;
    }
}
