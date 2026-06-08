package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    Long id; //ID пользователя

    @Email
    String email; //Почта пользователя

    String login; //Логин пользователя
    String name; //Отображаемое имя

    @PastOrPresent
    LocalDate birthday; //День рождения
}
