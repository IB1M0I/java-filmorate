package ru.yandex.practicum.filmorate.filmTest;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmSql;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class,
        UserDbStorage.class, UserRowMapper.class,
        DirectorDbStorage.class, DirectorRowMapper.class})
public class FilmDbStorageTest extends FilmSql {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final DirectorDbStorage directorStorage;

    //Тест создания фильма
    @Test
    public void createFilmTest() {
        Film film = Film.builder()
                .name("Фильм")
                .description("Описание")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new LinkedHashSet<>(Set.of(new Genre(1))))
                .mpa(new MpaRating(1, "G"))
                .build();
        Film filmSave = filmStorage.addFilm(film);

        Film filmFind = filmStorage.findById(filmSave.getId());

        Assertions.assertThat(filmFind).isNotNull();
        Assertions.assertThat(filmFind.getId()).isEqualTo(filmSave.getId());
        Assertions.assertThat(filmFind.getName()).isEqualTo(film.getName());
        Assertions.assertThat(filmFind.getReleaseDate()).isEqualTo(film.getReleaseDate());
        Assertions.assertThat(filmFind.getDuration()).isEqualTo(film.getDuration());
        Assertions.assertThat(filmFind.getMpa()).isEqualTo(film.getMpa());
    }

    //Тест поиска фильма по id
    @Test
    public void testFindFilmById() {
        Film film1 = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.now())
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();
        Film film2 = Film.builder()
                .name("Фильм2")
                .description("Описание2")
                .duration(200)
                .releaseDate(LocalDate.now())
                .genres(new LinkedHashSet<>(Set.of(new Genre(4))))
                .mpa(new MpaRating(1))
                .build();

        filmStorage.addFilm(film1);
        Film filmSave = filmStorage.addFilm(film2);

        Film findFilm = filmStorage.findById(filmSave.getId());

        Assertions.assertThat(findFilm).isNotNull();
        Assertions.assertThat(findFilm.getId()).isEqualTo(filmSave.getId());
        Assertions.assertThat(findFilm.getName()).isEqualTo("Фильм2");
        Assertions.assertThat(findFilm.getDescription()).isEqualTo("Описание2");
        Assertions.assertThat(findFilm.getDuration()).isEqualTo(200);
        Assertions.assertThat(findFilm.getReleaseDate()).isEqualTo(filmSave.getReleaseDate());

        Assertions.assertThat(findFilm.getMpa()).isNotNull();
        Assertions.assertThat(findFilm.getMpa().getId()).isEqualTo(1);

        Assertions.assertThat(findFilm.getGenres()).isNotEmpty();
        Assertions.assertThat(findFilm.getGenres().iterator().next().getId()).isEqualTo(4);

    }

    //Тест поиска фильма по несуществующему id
    @Test
    public void testFindUserById_NotFound() {
        Film film = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.now())
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();

        filmStorage.addFilm(film);

        Assertions.assertThatThrownBy(() -> filmStorage.findById(33))
                .isInstanceOf(NotFoundException.class);
    }

    //Тест обновления фильма
    @Test
    public void testUpdateFilm() {
        Film film = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.now())
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();
        Film saveFilm = filmStorage.addFilm(film);

        Film updateFilm = Film.builder()
                .id(film.getId())
                .name("Update Film")
                .description("Update description")
                .duration(200)
                .genres(new LinkedHashSet<>(Set.of(new Genre(1), new Genre(2))))
                .mpa(new MpaRating(1))
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();

        filmStorage.updateFilm(updateFilm);
        Film respone = filmStorage.findById(film.getId());
        Assertions.assertThat(respone).isNotNull();
        Assertions.assertThat(respone.getId()).isEqualTo(film.getId());
        Assertions.assertThat(respone.getName()).isEqualTo("Update Film");
        Assertions.assertThat(respone.getReleaseDate()).isEqualTo(LocalDate.of(2000, 1, 1));
        Assertions.assertThat(respone.getDuration()).isEqualTo(200);
        Assertions.assertThat(respone.getGenres()).isNotNull();
        Assertions.assertThat(respone.getGenres()).isEqualTo(Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")));
        Assertions.assertThat(respone.getMpa()).isNotNull();
        Assertions.assertThat(respone.getMpa().getId()).isEqualTo(1);
    }

    //Тест получения всех фильмов
    @Test
    public void testFindAllFilms() {
        Film film1 = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.now())
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();
        Film film2 = Film.builder()
                .name("Фильм2")
                .description("Описание2")
                .duration(200)
                .releaseDate(LocalDate.now())
                .genres(new LinkedHashSet<>(Set.of(new Genre(4))))
                .mpa(new MpaRating(1))
                .build();
        Film film3 = Film.builder()
                .name("Фильм3")
                .description("Описание3")
                .duration(100)
                .releaseDate(LocalDate.now())
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();

        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);

        Collection<Film> films = filmStorage.findAll();

        Assertions.assertThat(films).isNotNull();
        Assertions.assertThat(films).hasSize(3);

    }

    //Тест валидации даты выпуска фильма (до 28 декабря 1895)
    @Test
    public void filmReleaseDateBeforeFirstFilm() {
        Film film = new Film();

        film.setId(1L);
        film.setName("Фильм");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);
        film.setDescription("Описание");

        Assertions.assertThatThrownBy(() -> filmStorage.addFilm(film)).isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Самые популярные фильмы")
    void testGetPopularFilms() {
        Film film1 = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.now())
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();
        Film film2 = Film.builder()
                .name("Фильм2")
                .description("Описание2")
                .duration(200)
                .releaseDate(LocalDate.now())
                .genres(new LinkedHashSet<>(Set.of(new Genre(4))))
                .mpa(new MpaRating(1))
                .build();
        Film film3 = Film.builder()
                .name("Фильм3")
                .description("Описание3")
                .duration(100)
                .releaseDate(LocalDate.now())
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);

        User user1 = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();
        User user2 = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();
        User user3 = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);

        filmStorage.likeFilm(film1.getId(), user1.getId());
        filmStorage.likeFilm(film2.getId(), user1.getId());
        filmStorage.likeFilm(film2.getId(), user2.getId());
        filmStorage.likeFilm(film2.getId(), user3.getId());
        filmStorage.likeFilm(film3.getId(), user2.getId());
        filmStorage.likeFilm(film3.getId(), user3.getId());

        Collection<Film> popularFilms = filmStorage.getPopularFilmsByGenreIdByYear(2, null, null);

        assertThat(popularFilms)
                .isNotNull()
                .hasSize(2)
                .extracting(Film::getId)
                .containsExactly(film2.getId(), film3.getId());
    }

    @Test
    @DisplayName("Самые популярные фильмы по жанру")
    void testGetPopularFilmsByGenre() {
        Film film1 = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.now())
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();
        Film film2 = Film.builder()
                .name("Фильм2")
                .description("Описание2")
                .duration(200)
                .releaseDate(LocalDate.now())
                .genres(new LinkedHashSet<>(Set.of(new Genre(4))))
                .mpa(new MpaRating(1))
                .build();
        Film film3 = Film.builder()
                .name("Фильм3")
                .description("Описание3")
                .duration(100)
                .releaseDate(LocalDate.now())
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);

        User user1 = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();
        User user2 = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();
        User user3 = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);

        filmStorage.likeFilm(film1.getId(), user1.getId());
        filmStorage.likeFilm(film2.getId(), user1.getId());
        filmStorage.likeFilm(film2.getId(), user2.getId());
        filmStorage.likeFilm(film2.getId(), user3.getId());
        filmStorage.likeFilm(film3.getId(), user2.getId());
        filmStorage.likeFilm(film3.getId(), user3.getId());

        Collection<Film> popularFilms = filmStorage.getPopularFilmsByGenreIdByYear(2, 4, null);

        assertThat(popularFilms)
                .isNotNull()
                .hasSize(1)
                .extracting(Film::getId)
                .containsExactly(film2.getId());
    }

    @Test
    @DisplayName("Самые популярные фильмы по году")
    void testGetPopularFilmsByYear() {
        Film film1 = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.of(1985, 11, 30))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();
        Film film2 = Film.builder()
                .name("Фильм2")
                .description("Описание2")
                .duration(200)
                .releaseDate(LocalDate.of(1957, 9, 11))
                .genres(new LinkedHashSet<>(Set.of(new Genre(4))))
                .mpa(new MpaRating(1))
                .build();
        Film film3 = Film.builder()
                .name("Фильм3")
                .description("Описание3")
                .duration(100)
                .releaseDate(LocalDate.of(1987, 6, 12))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);

        User user1 = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();
        User user2 = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();
        User user3 = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);

        filmStorage.likeFilm(film1.getId(), user1.getId());
        filmStorage.likeFilm(film2.getId(), user1.getId());
        filmStorage.likeFilm(film2.getId(), user2.getId());
        filmStorage.likeFilm(film2.getId(), user3.getId());
        filmStorage.likeFilm(film3.getId(), user2.getId());
        filmStorage.likeFilm(film3.getId(), user3.getId());

        Collection<Film> popularFilms = filmStorage.getPopularFilmsByGenreIdByYear(2, null, 1987);

        assertThat(popularFilms)
                .isNotNull()
                .hasSize(1)
                .extracting(Film::getId)
                .containsExactly(film3.getId());
    }

    @Test
    @DisplayName("Самые популярные фильмы по жанру и году")
    void testGetPopularFilmsByGenreByYear() {
        Film film1 = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.of(1985, 11, 30))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();
        Film film2 = Film.builder()
                .name("Фильм2")
                .description("Описание2")
                .duration(200)
                .releaseDate(LocalDate.of(1957, 9, 11))
                .genres(new LinkedHashSet<>(Set.of(new Genre(4))))
                .mpa(new MpaRating(1))
                .build();
        Film film3 = Film.builder()
                .name("Фильм3")
                .description("Описание3")
                .duration(100)
                .releaseDate(LocalDate.of(1987, 6, 12))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);

        User user1 = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();
        User user2 = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();
        User user3 = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);

        filmStorage.likeFilm(film1.getId(), user1.getId());
        filmStorage.likeFilm(film2.getId(), user1.getId());
        filmStorage.likeFilm(film2.getId(), user2.getId());
        filmStorage.likeFilm(film2.getId(), user3.getId());
        filmStorage.likeFilm(film3.getId(), user2.getId());
        filmStorage.likeFilm(film3.getId(), user3.getId());

        Collection<Film> popularFilms = filmStorage.getPopularFilmsByGenreIdByYear(2, 2, 1987);

        assertThat(popularFilms)
                .isNotNull()
                .hasSize(1)
                .extracting(Film::getId)
                .containsExactly(film3.getId());
    }

    @Test
    @DisplayName("Поиск фильмов по названию")
    void testSearchFilmsByTitleByDirectorWhenTitleReturnsFilms() {
        Director director1 = new Director(1L, "Режиссёр1");
        Director director2 = new Director(2L, "Режиссёр2");
        Director director3 = new Director(3L, "Режиссёр3");
        directorStorage.create(director1);
        directorStorage.create(director2);
        directorStorage.create(director3);

        Film film1 = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.of(1985, 11, 30))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .directors(Set.of(director1))
                .build();
        Film film2 = Film.builder()
                .name("Фильм2")
                .description("Описание2")
                .duration(200)
                .releaseDate(LocalDate.of(1957, 9, 11))
                .genres(new LinkedHashSet<>(Set.of(new Genre(4))))
                .mpa(new MpaRating(1))
                .directors(Set.of(director2))
                .build();
        Film film3 = Film.builder()
                .name("Фильм3")
                .description("Описание3")
                .duration(100)
                .releaseDate(LocalDate.of(1987, 6, 12))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .directors(Set.of(director3))
                .build();
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);

        Collection<Film> films = filmStorage.searchFilmsByTitleByDirector("фильм", true, false);

        assertThat(films)
                .isNotNull()
                .hasSize(3)
                .extracting(Film::getId)
                .containsExactly(film1.getId(), film2.getId(), film3.getId());
    }

    @Test
    @DisplayName("Поиск по режиссёру")
    void testSearchFilmsByTitleByDirectorWhenDirectorReturnsFilms() {
        Director director1 = new Director(1L, "Режиссёр1");
        Director director2 = new Director(2L, "Режиссёр2");
        Director director3 = new Director(3L, "Режиссёр3");
        directorStorage.create(director1);
        directorStorage.create(director2);
        directorStorage.create(director3);

        Film film1 = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.of(1985, 11, 30))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .directors(Set.of(director1))
                .build();
        Film film2 = Film.builder()
                .name("Фильм2")
                .description("Описание2")
                .duration(200)
                .releaseDate(LocalDate.of(1957, 9, 11))
                .genres(new LinkedHashSet<>(Set.of(new Genre(4))))
                .mpa(new MpaRating(1))
                .directors(Set.of(director2))
                .build();
        Film film3 = Film.builder()
                .name("Фильм3")
                .description("Описание3")
                .duration(100)
                .releaseDate(LocalDate.of(1987, 6, 12))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .directors(Set.of(director3))
                .build();
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);

        Collection<Film> films = filmStorage.searchFilmsByTitleByDirector("режиссёр", false, true);

        assertThat(films)
                .isNotNull()
                .hasSize(3)
                .extracting(Film::getId)
                .containsExactly(film1.getId(), film2.getId(), film3.getId());
    }

    @Test
    @DisplayName("Поиск по названию фильмов и по режиссёру")
    void testSearchFilmsByTitleByDirectorWhenTitleAndDirectorReturnsFilms() {
        Director director1 = new Director(1L, "Режиссёр1");
        Director director2 = new Director(2L, "Режиссёр2");
        Director director3 = new Director(3L, "Режиссёр3");
        directorStorage.create(director1);
        directorStorage.create(director2);
        directorStorage.create(director3);

        Film film1 = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.of(1985, 11, 30))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .directors(Set.of(director1))
                .build();
        Film film2 = Film.builder()
                .name("Фильм2")
                .description("Описание2")
                .duration(200)
                .releaseDate(LocalDate.of(1957, 9, 11))
                .genres(new LinkedHashSet<>(Set.of(new Genre(4))))
                .mpa(new MpaRating(1))
                .directors(Set.of(director2))
                .build();
        Film film3 = Film.builder()
                .name("Фильм3")
                .description("Описание3")
                .duration(100)
                .releaseDate(LocalDate.of(1987, 6, 12))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .directors(Set.of(director3))
                .build();
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);

        Collection<Film> films = filmStorage.searchFilmsByTitleByDirector("и", true, true);

        assertThat(films)
                .isNotNull()
                .hasSize(3)
                .extracting(Film::getId)
                .containsExactly(film1.getId(), film2.getId(), film3.getId());
    }

    @Test
    @DisplayName("Поиск по неизвестному параметру")
    void testSearchFilmsByTitleByDirectorWhenUnknownParameterReturnsEmptyList() {
        Director director1 = new Director(1L, "Режиссёр1");
        Director director2 = new Director(2L, "Режиссёр2");
        Director director3 = new Director(3L, "Режиссёр3");
        directorStorage.create(director1);
        directorStorage.create(director2);
        directorStorage.create(director3);

        Film film1 = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.of(1985, 11, 30))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .directors(Set.of(director1))
                .build();
        Film film2 = Film.builder()
                .name("Фильм2")
                .description("Описание2")
                .duration(200)
                .releaseDate(LocalDate.of(1957, 9, 11))
                .genres(new LinkedHashSet<>(Set.of(new Genre(4))))
                .mpa(new MpaRating(1))
                .directors(Set.of(director2))
                .build();
        Film film3 = Film.builder()
                .name("Фильм3")
                .description("Описание3")
                .duration(100)
                .releaseDate(LocalDate.of(1987, 6, 12))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .directors(Set.of(director3))
                .build();
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);

        Collection<Film> films = filmStorage.searchFilmsByTitleByDirector("и", false, false);
        assertThat(films).isEmpty();
    }

    @Test
    @DisplayName("Поиск фильмов по названию - возвращает пустой список")
    void testSearchFilmsByTitleByDirectorWhenTitleReturnsEmpty() {
        Director director1 = new Director(1L, "Режиссёр1");
        Director director2 = new Director(2L, "Режиссёр2");
        Director director3 = new Director(3L, "Режиссёр3");
        directorStorage.create(director1);
        directorStorage.create(director2);
        directorStorage.create(director3);

        Film film1 = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.of(1985, 11, 30))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .directors(Set.of(director1))
                .build();
        Film film2 = Film.builder()
                .name("Фильм2")
                .description("Описание2")
                .duration(200)
                .releaseDate(LocalDate.of(1957, 9, 11))
                .genres(new LinkedHashSet<>(Set.of(new Genre(4))))
                .mpa(new MpaRating(1))
                .directors(Set.of(director2))
                .build();
        Film film3 = Film.builder()
                .name("Фильм3")
                .description("Описание3")
                .duration(100)
                .releaseDate(LocalDate.of(1987, 6, 12))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .directors(Set.of(director3))
                .build();
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);

        Collection<Film> films = filmStorage.searchFilmsByTitleByDirector("описание", true, false);
        assertThat(films).isEmpty();
    }

    @Test
    @DisplayName("Поиск по режиссёру - возвращает пустой список")
    void testSearchFilmsByTitleByDirectorWhenDirectorReturnsEmpty() {
        Director director1 = new Director(1L, "Режиссёр1");
        Director director2 = new Director(2L, "Режиссёр2");
        Director director3 = new Director(3L, "Режиссёр3");
        directorStorage.create(director1);
        directorStorage.create(director2);
        directorStorage.create(director3);

        Film film1 = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.of(1985, 11, 30))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .directors(Set.of(director1))
                .build();
        Film film2 = Film.builder()
                .name("Фильм2")
                .description("Описание2")
                .duration(200)
                .releaseDate(LocalDate.of(1957, 9, 11))
                .genres(new LinkedHashSet<>(Set.of(new Genre(4))))
                .mpa(new MpaRating(1))
                .directors(Set.of(director2))
                .build();
        Film film3 = Film.builder()
                .name("Фильм3")
                .description("Описание3")
                .duration(100)
                .releaseDate(LocalDate.of(1987, 6, 12))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .directors(Set.of(director3))
                .build();
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);

        Collection<Film> films = filmStorage.searchFilmsByTitleByDirector("описание", false, true);
        assertThat(films).isEmpty();
    }

    @Test
    @DisplayName("Поиск по названию фильмов и по режиссёру - возвращает пустой список")
    void testSearchFilmsByTitleByDirectorWhenTitleAndDirectorReturnsEmpty() {
        Director director1 = new Director(1L, "Режиссёр1");
        Director director2 = new Director(2L, "Режиссёр2");
        Director director3 = new Director(3L, "Режиссёр3");
        directorStorage.create(director1);
        directorStorage.create(director2);
        directorStorage.create(director3);

        Film film1 = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.of(1985, 11, 30))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .directors(Set.of(director1))
                .build();
        Film film2 = Film.builder()
                .name("Фильм2")
                .description("Описание2")
                .duration(200)
                .releaseDate(LocalDate.of(1957, 9, 11))
                .genres(new LinkedHashSet<>(Set.of(new Genre(4))))
                .mpa(new MpaRating(1))
                .directors(Set.of(director2))
                .build();
        Film film3 = Film.builder()
                .name("Фильм3")
                .description("Описание3")
                .duration(100)
                .releaseDate(LocalDate.of(1987, 6, 12))
                .genres(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .directors(Set.of(director3))
                .build();
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);

        Collection<Film> films = filmStorage.searchFilmsByTitleByDirector("описание", true, true);
        assertThat(films).isEmpty();
    }
}
