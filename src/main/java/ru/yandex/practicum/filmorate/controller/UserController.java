package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    Map<Long, User> users = new HashMap<>();

    //Возвращает список всех пользователей
    @GetMapping
    public Collection<User> getUsers() {
        log.trace("Вызван метод получения списка пользователей");
        log.info("Вернули список пользователей");
        return users.values();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("email не может быть пустым");
            throw new ValidationException("email не может быть пустым");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.error("login не может быть пустым");
            throw new ValidationException("login не может быть пустым");
        }
        if(user.getLogin().contains(" ")){
            log.error("login не может содержать пробелы");
            throw new ValidationException("login не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("name пустой, в name присвоен login");
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        log.trace("Пользователю {} присвоен id = {}", user.getName(), user.getId());
        users.put(user.getId(), user);
        log.info("В список добавлен пользователь {} с id = {}", user.getName(), user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.trace("Вызван метод обновления пользователя");
        if (user.getId() == null) {
            log.error("id не может быть пустым");
            throw new ValidationException("id не может быть пустым");
        }

        User oldUser = users.get(user.getId());
        log.trace("Найдет user с id = {}", user.getId());

        log.trace("Обновление данных user с id = {}", oldUser.getId());
        oldUser.setName(user.getName() != null ? user.getName() : oldUser.getName());
        oldUser.setLogin(user.getLogin() != null ? user.getLogin() : oldUser.getLogin());
        oldUser.setBirthday(user.getBirthday() != null ? user.getBirthday() : oldUser.getBirthday());
        oldUser.setEmail(user.getEmail() != null ? user.getEmail() : oldUser.getEmail());

        log.info("В список внесены изменения пользователя с id = {}", user.getId());
        return user;
    }


    private long getNextId() {
        long currentId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        log.trace("Сгенерирован новый id");
        return ++currentId;
    }
}
