package ru.yandex.practicum.filmorate.userTest;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
public class UserDbStorageTest {
    private final UserDbStorage userDbStorage;

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
}
