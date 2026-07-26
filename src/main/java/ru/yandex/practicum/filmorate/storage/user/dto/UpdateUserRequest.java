package ru.yandex.practicum.filmorate.storage.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    @NotNull(message = "id не может быть пустым")
    private Long id;
    private String email; //Почта пользователя
    private String login; //Логин пользователя
    private String name; //Отображаемое имя

    @PastOrPresent(message = "Дата рождения не может быть указана в будущем")
    private LocalDate birthday; //День рождения

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

    public boolean hasLogin() {
        return !(login == null || login.isBlank());
    }

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasBirthday() {
        return  birthday != null;
    }
}
