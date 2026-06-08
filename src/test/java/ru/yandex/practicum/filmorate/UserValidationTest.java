package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

public class UserValidationTest {
    private UserController userController = new UserController();
    private Set<ConstraintViolation<User>> validations;
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void userEmailIsEmpty() {
        User user = new User();
        user.setId(1L);
        user.setLogin("login");
        user.setName("name");
        user.setEmail("");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    public void userEmailWithoutAtSign() {
        User user = new User();
        user.setId(1L);
        user.setLogin("login");
        user.setName("name");
        user.setEmail("почта.ru");
        user.setBirthday(LocalDate.of(1985, 11, 3));

        validations = validator.validate(user);

        Assertions.assertFalse(validations.isEmpty());
        Assertions.assertTrue(validations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        Assertions.assertTrue(validations.stream().anyMatch(v -> v.getMessage().equals("Введен не верный формат почты")));
    }

    @Test
    public void userLoginIsEmpty() {
        User user = new User();
        user.setId(1L);
        user.setLogin("");
        user.setName("name");
        user.setEmail("почта@yandex.ru");
        user.setBirthday(LocalDate.of(2000, 1, 28));

        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    public void userLoginContainsSpace() {
        User user = new User();
        user.setId(1L);
        user.setLogin("log in");
        user.setName("name");
        user.setEmail("почта@yandex.ru");
        user.setBirthday(LocalDate.of(1978, 7, 19));

        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    public void userNameIsEmptyUsesLogin() {
        User user = new User();
        user.setId(1L);
        user.setLogin("login");
        user.setName("");
        user.setEmail("почта@yandex.ru");
        user.setBirthday(LocalDate.of(1995, 3, 7));

        User create = userController.addUser(user);

        Assertions.assertEquals(user.getLogin(), create.getName());
    }

    @Test
    public void userBirthdayInFuture() {
        User user = new User();
        user.setId(1L);
        user.setLogin("login");
        user.setName("name");
        user.setEmail("почта@yandex.ru");
        user.setBirthday(LocalDate.now().plusDays(1));

        validations = validator.validate(user);

        Assertions.assertFalse(validations.isEmpty());
        Assertions.assertTrue(validations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));
        Assertions.assertTrue(validations.stream().anyMatch(v -> v.getMessage().equals("Дата рождения не может быть указана в будущем")));
    }

    @Test
    public void userBirthdayIsToday() {
        User user = new User();
        user.setId(1L);
        user.setLogin("login");
        user.setName("name");
        user.setEmail("почта@yandex.ru");
        user.setBirthday(LocalDate.now());

        User create = userController.addUser(user);

        Assertions.assertEquals(user, create);
    }
}
