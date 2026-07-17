package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.exeption.ValidationException;

import java.util.Objects;

@Getter
@NoArgsConstructor(force = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MpaRating {
    private  int id;
    private String name;

    public MpaRating(int id){
        this.id = id;

    }
    public MpaRating(int id, String name){
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MpaRating mpaRating = (MpaRating) o;
        return id == mpaRating.id && Objects.equals(name, mpaRating.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
