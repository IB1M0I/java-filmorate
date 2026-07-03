package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id; //ID пользователя

    @Email(message = "Введен не верный формат почты")
    private String email; //Почта пользователя

    private String login; //Логин пользователя
    private String name; //Отображаемое имя

    @PastOrPresent(message = "Дата рождения не может быть указана в будущем")
    private LocalDate birthday; //День рождения

    private Set<Long> friends = new HashSet<>();
}
