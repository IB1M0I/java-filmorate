package ru.yandex.practicum.filmorate.userTest;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class,
        FilmDbStorage.class, FilmRowMapper.class})
public class UserDbStorageTest {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        jdbc.execute("SET REFERENTIAL_INTEGRITY FALSE");

        jdbc.execute("DELETE FROM likes_movies");
        jdbc.execute("DELETE FROM films");
        jdbc.execute("DELETE FROM users");

        jdbc.execute("SET REFERENTIAL_INTEGRITY TRUE");

        jdbc.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
        jdbc.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
    }

    //Тест создания пользователя
    @Test
    public void createUserTest() {
        User user = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();

        User findUser = userDbStorage.addUser(user);
        Assertions.assertThat(findUser).isNotNull();
        Assertions.assertThat(findUser.getId()).isNotNull();
        Assertions.assertThat(findUser.getLogin()).isEqualTo("login");
        Assertions.assertThat(findUser.getEmail()).isEqualTo("email@mail.com");
        Assertions.assertThat(findUser.getBirthday()).isEqualTo(LocalDate.now());
        Assertions.assertThat(findUser.getName()).isEqualTo("Имя");
    }

    //Тест поиска пользователя по id
    @Test
    public void testFindUserById() {
        User user = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();
        User user2 = User.builder()
                .name("Имя2")
                .login("login2")
                .email("email2@mail.com")
                .birthday(LocalDate.now())
                .build();

        User saveUser = userDbStorage.addUser(user);
        userDbStorage.addUser(user2);

        User findUser = userDbStorage.findById(saveUser.getId());

        Assertions.assertThat(saveUser).isNotNull();
        Assertions.assertThat(findUser).isNotNull();
        Assertions.assertThat(findUser.getId()).isEqualTo(user.getId());
        Assertions.assertThat(findUser.getLogin()).isEqualTo(user.getLogin());
        Assertions.assertThat(findUser.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(findUser.getBirthday()).isEqualTo(user.getBirthday());
        Assertions.assertThat(findUser.getName()).isEqualTo(user.getName());
    }

    //Тест поиска пользователя по несуществующему id
    @Test
    public void testFindUserById_NotFound() {
        User user1 = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();
        User user2 = User.builder()
                .name("Имя2")
                .login("login2")
                .email("email2@mail.com")
                .birthday(LocalDate.now())
                .build();

        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);

        Assertions.assertThatThrownBy(() -> userDbStorage.findById(33)).isInstanceOf(NotFoundException.class);
    }

    //Тест обновления пользователя
    @Test
    public void testUpdateUser() {
        User user = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();
        User updateUser = User.builder()
                .name("New name")
                .login("New login")
                .email("Newemail@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        userDbStorage.addUser(user);
        User request = userDbStorage.updateUser(updateUser);

        Assertions.assertThat(request).isNotNull();
        Assertions.assertThat(request.getLogin()).isEqualTo("New login");
        Assertions.assertThat(request.getEmail()).isEqualTo("Newemail@mail.com");
        Assertions.assertThat(request.getBirthday()).isEqualTo(LocalDate.of(2000, 1, 1));
        Assertions.assertThat(request.getName()).isEqualTo("New name");
    }

    //Тест получения всех пользователей
    @Test
    public void testFindAllUsers() {
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

        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);
        userDbStorage.addUser(user3);

        Collection<User> users = userDbStorage.findAll();
        Assertions.assertThat(users).isNotNull();
        Assertions.assertThat(users.size()).isEqualTo(3);
    }

    //Тест добавления друга
    @Test
    public void testAddFriend() {
        User user = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();
        User friend = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();

        User saveUser = userDbStorage.addUser(user);
        User saveFriend = userDbStorage.addUser(friend);

        User usetAdd = userDbStorage.addFriend(user.getId(), friend.getId(), false);

        Assertions.assertThat(userDbStorage.getFriends(saveUser.getId())).hasSize(1);
    }

    //Тест удаления друга
    @Test
    public void testRemoveFriend() {
        User user = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();
        User friend = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();

        User saveUser = userDbStorage.addUser(user);
        User saveFriend = userDbStorage.addUser(friend);

        User usetAdd = userDbStorage.addFriend(user.getId(), friend.getId(), false);

        Assertions.assertThat(userDbStorage.getFriends(saveUser.getId())).hasSize(1);
        userDbStorage.deleteFriend(saveUser.getId(), saveFriend.getId());
        Assertions.assertThat(userDbStorage.getFriends(saveUser.getId())).hasSize(0);
    }

    @Test
    @DisplayName("Рекомендации непустой список")
    void testGetRecommendationsWhenOneFromThreeFilmsEvaluatedReturnsFilms() {
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
        filmDbStorage.addFilm(film1);
        filmDbStorage.addFilm(film2);
        filmDbStorage.addFilm(film3);

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

        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);
        userDbStorage.addUser(user3);

        filmDbStorage.likeFilm(film1.getId(), user1.getId());
        filmDbStorage.likeFilm(film2.getId(), user1.getId());
        filmDbStorage.likeFilm(film2.getId(), user2.getId());
        filmDbStorage.likeFilm(film2.getId(), user3.getId());
        filmDbStorage.likeFilm(film3.getId(), user1.getId());
        filmDbStorage.likeFilm(film3.getId(), user3.getId());

        Collection<Film> recommendations = userDbStorage.getRecommendations(2);

        assertThat(recommendations)
                .isNotNull()
                .hasSize(2)
                .extracting(Film::getId)
                .containsExactly(film3.getId(), film1.getId());
    }

    @Test
    @DisplayName("Рекомендации пустой список")
    void testGetRecommendationsWhenAllFilmsEvaluatedReturnsEmpty() {
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
        filmDbStorage.addFilm(film1);
        filmDbStorage.addFilm(film2);
        filmDbStorage.addFilm(film3);

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

        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);
        userDbStorage.addUser(user3);

        filmDbStorage.likeFilm(film1.getId(), user1.getId());
        filmDbStorage.likeFilm(film1.getId(), user2.getId());
        filmDbStorage.likeFilm(film2.getId(), user1.getId());
        filmDbStorage.likeFilm(film2.getId(), user2.getId());
        filmDbStorage.likeFilm(film2.getId(), user3.getId());
        filmDbStorage.likeFilm(film3.getId(), user1.getId());
        filmDbStorage.likeFilm(film3.getId(), user2.getId());
        filmDbStorage.likeFilm(film3.getId(), user3.getId());

        Collection<Film> recommendations = userDbStorage.getRecommendations(user2.getId());

        assertThat(recommendations).isEmpty();
    }

    //Тест добавления события
    @Test
    public void testAddEvent_WhenEventAdded_EventReturned() {
        User user = User.builder()
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

        User saveUser = userDbStorage.addUser(user);
        User saveUser2 = userDbStorage.addUser(user2);
        long timestamp = Instant.now().toEpochMilli();
        long userId = saveUser.getId();

        userDbStorage.addEvent(timestamp, userId, EventType.FRIEND, Operation.ADD, saveUser2.getId());

        Collection<Event> events = userDbStorage.getEventsUser(userId);
        Assertions.assertThat(events).isNotNull();
        Assertions.assertThat(events).hasSize(1);

        Event event = events.iterator().next();
        Assertions.assertThat(event.getTimestamp()).isEqualTo(timestamp);
        Assertions.assertThat(event.getUserId()).isEqualTo(userId);
        Assertions.assertThat(event.getEventType()).isEqualTo(EventType.FRIEND);
        Assertions.assertThat(event.getOperation()).isEqualTo(Operation.ADD);
        Assertions.assertThat(event.getEntityId()).isEqualTo(saveUser2.getId());
    }

    //Тест получения событий пользователя
    @Test
    public void testGetEventsUser_WhenEventsExist_EventsReturned() {
        User user = User.builder()
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

        User saveUser = userDbStorage.addUser(user);
        User saveUser2 = userDbStorage.addUser(user2);

        long userId = saveUser.getId();

        userDbStorage.addEvent(Instant.now().toEpochMilli(), userId, EventType.FRIEND, Operation.ADD, saveUser2.getId());

        Collection<Event> events = userDbStorage.getEventsUser(userId);
        Assertions.assertThat(events).isNotNull();
        Assertions.assertThat(events).hasSize(1);
    }

    //Тест получения событий когда их нет
    @Test
    public void testGetEventsUser_WhenNoEvents_EmptyListReturned() {
        User user = User.builder()
                .name("Имя")
                .login("login")
                .email("email@mail.com")
                .birthday(LocalDate.now())
                .build();

        User saveUser = userDbStorage.addUser(user);

        Collection<Event> events = userDbStorage.getEventsUser(saveUser.getId());
        Assertions.assertThat(events).isNotNull();
        Assertions.assertThat(events).isEmpty();
    }
}