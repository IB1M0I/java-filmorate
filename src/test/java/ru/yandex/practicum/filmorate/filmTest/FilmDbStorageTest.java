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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmSql;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class,
        UserDbStorage.class, UserRowMapper.class})
public class FilmDbStorageTest extends FilmSql {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

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

    //Тест поиска общих фильмов
    @Test
    public void getCommonFilms_WhenUsersHaveCommonFilms_ReturnsListOfSharedFilms() {
        Film film1 = Film.builder()
                .name("Фильм1")
                .description("Описание1")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"))))
                .mpa(new MpaRating(1, "G"))
                .build();

        Film film2 = Film.builder()
                .name("Фильм2")
                .description("Описание2")
                .releaseDate(LocalDate.now())
                .duration(150)
                .genres(new LinkedHashSet<>(Set.of(new Genre(2, "Драмма"))))
                .mpa(new MpaRating(2, "PG"))
                .build();

        Film film3 = Film.builder()
                .name("Фильм3")
                .description("Описание3")
                .releaseDate(LocalDate.now())
                .duration(180)
                .genres(new LinkedHashSet<>(Set.of(new Genre(3, "Мультфильм"))))
                .mpa(new MpaRating(3, "PG-13"))
                .build();

        User user1 = User.builder()
                .email("user1@example.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User user2 = User.builder()
                .email("user2@example.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1995, 5, 15))
                .build();

        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);

        userStorage.addUser(user1);
        userStorage.addUser(user2);

        filmStorage.likeFilm(film1.getId(), user1.getId());
        filmStorage.likeFilm(film1.getId(), user2.getId());
        filmStorage.likeFilm(film2.getId(), user1.getId());

        film1.setLikes(Set.of(user1.getId(), user2.getId()));
        Collection<Film> expectedFilms = List.of(film1);

        Collection<Film> films = filmStorage.getCommonFilms(user1.getId(), user2.getId());

        Assertions.assertThat(films).isNotNull();
        Assertions.assertThat(films).hasSize(1);
        Assertions.assertThat(films).hasSameElementsAs(expectedFilms);
    }

    //Тест поиска общих фильмов (общих фильмов нет)
    @Test
    public void getCommonFilms_WhenNoCommonFilms_ReturnsEmptyList() {
        Film film1 = Film.builder()
                .name("Фильм1")
                .description("Описание1")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genres(new LinkedHashSet<>(Set.of(new Genre(1, "Комедия"))))
                .mpa(new MpaRating(1, "G"))
                .build();

        Film film2 = Film.builder()
                .name("Фильм2")
                .description("Описание2")
                .releaseDate(LocalDate.now())
                .duration(150)
                .genres(new LinkedHashSet<>(Set.of(new Genre(2, "Драмма"))))
                .mpa(new MpaRating(2, "PG"))
                .build();

        Film film3 = Film.builder()
                .name("Фильм3")
                .description("Описание3")
                .releaseDate(LocalDate.now())
                .duration(180)
                .genres(new LinkedHashSet<>(Set.of(new Genre(3, "Мультфильм"))))
                .mpa(new MpaRating(3, "PG-13"))
                .build();

        User user1 = User.builder()
                .email("user1@example.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User user2 = User.builder()
                .email("user2@example.com")
                .login("user2")
                .name("User Two")
                .birthday(LocalDate.of(1995, 5, 15))
                .build();

        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);

        userStorage.addUser(user1);
        userStorage.addUser(user2);

        Assertions.assertThat(filmStorage.getCommonFilms(1, 2)).isEmpty();
    }
}
