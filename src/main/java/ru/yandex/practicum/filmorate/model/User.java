package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class User {
    Long id; //ID пользователя

    @Email(message = "Введен не верный формат почты")
    String email; //Почта пользователя

    String login; //Логин пользователя
    String name; //Отображаемое имя

    @PastOrPresent(message = "Дата рождения не может быть указана в будущем")
    LocalDate birthday; //День рождения

    Set<Long> friends;
}
