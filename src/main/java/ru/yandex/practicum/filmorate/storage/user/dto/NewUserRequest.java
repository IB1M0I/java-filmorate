package ru.yandex.practicum.filmorate.storage.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewUserRequest {
    @Email(message = "Введен не верный формат почты")
    @NotBlank(message = "Email не может быть пустым")
    private String email; //Почта пользователя

    @NotBlank(message = "Login не может быть пустым")
    private String login; //Логин пользователя
    private String name; //Отображаемое имя

    @NotNull(message = "Дата рождения не может быть пустой")
    @PastOrPresent(message = "Дата рождения не может быть указана в будущем")
    private LocalDate birthday; //День рождения
}
