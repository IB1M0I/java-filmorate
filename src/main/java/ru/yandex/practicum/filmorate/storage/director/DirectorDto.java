package ru.yandex.practicum.filmorate.storage.director;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DirectorDto {
    private Long id;

    @NotBlank(message = "Имя режиссёра не может быть пустым")
    private String name;
}
