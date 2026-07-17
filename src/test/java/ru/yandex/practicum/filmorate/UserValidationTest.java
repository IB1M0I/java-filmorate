package ru.yandex.practicum.filmorate;

import jakarta.validation.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

public class UserValidationTest {
    private Validator validator;
    private Set<ConstraintViolation<User>> validations;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
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

}
