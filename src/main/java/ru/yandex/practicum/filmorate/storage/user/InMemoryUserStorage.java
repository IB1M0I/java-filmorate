package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    Map<Long, User> users = new HashMap<>();

    //Добавить пользователя
    @Override
    public User addUser(@Valid @RequestBody User user) {
        log.trace("Вызван метод addUser");
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("email не может быть пустым");
            throw new ValidationException("email не может быть пустым");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.error("login не может быть пустым");
            throw new ValidationException("login не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
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

    //Обновить информацию о пользователе
    @Override
    public User updateUser(@Valid @RequestBody User user) {
        log.trace("Вызван метод updateUser");
        if (user.getId() == null) {
            log.error("id не может быть пустым");
            throw new ValidationException("id не может быть пустым");
        }
        if (users.get(user.getId()) == null) {
            log.error("Пользователь с id = {} не найден", user.getId());
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", user.getId()));

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

    //Удалить пользователя
    @Override
    public User deleteUser(long id) {
        log.trace("Вызван метод deleteUser");
        log.debug("id = {}", id);

        if (!users.containsKey(id)) {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }

        User user = users.get(id);

        users.remove(id);
        log.info("Пользователь с id = {} удален", id);
        return user;
    }

    //Получить пользователя по id
    @Override
    public User findById(long id) {
        log.trace("Вызван метод поиска по id");
        if (!users.containsKey(id)) {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }

        return users.get(id);
    }

    //Получить всех пользователей
    @Override
    public Collection<User> findAll() {
        log.trace("Вызван метод получения списка пользователей");
        log.info("Вернули список пользователей");
        return users.values();
    }


    //Генерация id
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
