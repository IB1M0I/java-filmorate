package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id; //ID пользователя

    @Email(message = "Введен не верный формат почты")
    @NotBlank(message = "Email не может быть пустым")
    private String email; //Почта пользователя

    @NotBlank(message = "Login не может быть пустым")
    private String login; //Логин пользователя
    private String name; //Отображаемое имя

    @PastOrPresent(message = "Дата рождения не может быть указана в будущем")
    private LocalDate birthday; //День рождения
    private Set<Long> friends = new HashSet<>();
}
