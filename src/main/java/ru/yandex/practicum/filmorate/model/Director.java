package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Director {
    private Long id;

    @NotBlank(message = "Имя режиссёра не может быть пустым")
    private String name;

    public Director(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
