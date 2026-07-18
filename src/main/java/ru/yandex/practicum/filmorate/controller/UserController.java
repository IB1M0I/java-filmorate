package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.storage.user.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.storage.user.dto.UserDto;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    //Добавить пользователя
    @PostMapping
    public UserDto addUser(@Valid @RequestBody NewUserRequest request) {
        log.debug("Получен запрос на добавление пользователя: {}", request.getLogin());
        UserDto user = userService.addUser(request);
        log.info("Пользователь успешно добавлен с id: {}", user.getId());
        return user;
    }

    //Получить список всех пользователей
    @GetMapping
    public Collection<UserDto> findAll() {
        log.debug("Получен запрос на получение всех пользователей");
        Collection<UserDto> users = userService.findAll();
        log.info("Получено {} пользователей", users.size());
        return users;
    }

    //Обновить данные пользователя
    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UpdateUserRequest request) {
        log.debug("Получен запрос на обновление пользователя с id: {}", request.getId());
        UserDto user = userService.updateUser(request);
        log.info("Пользователь с id {} успешно обновлен", user.getId());
        return user;
    }


    //Удалить пользователя
    @DeleteMapping("/{id}")
    public UserDto deleteUser(@PathVariable long id) {
        log.debug("Получен запрос на удаление пользователя с id: {}", id);
        UserDto user = userService.deleteUser(id);
        log.info("Пользователь с id {} успешно удален", id);
        return user;
    }

    //Получить пользователя по id
    @GetMapping("/{id}")
    public UserDto findById(@PathVariable long id) {
        log.debug("Получен запрос на получение пользователя с id: {}", id);
        UserDto user = userService.findById(id);
        log.info("Пользователь с id {} успешно получен", id);
        return user;
    }

    //Добавить друга
    @PutMapping("/{id}/friends/{friendId}")
    public UserDto addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("Пользователь {} добавляет в друзья пользователя {}", id, friendId);
        UserDto user = userService.addFriend(id, friendId);
        log.info("Пользователь {} успешно добавлен в друзья пользователю {}", friendId, id);
        return user;
    }

    //Получить список друзей пользователя
    @GetMapping("/{id}/friends")
    public Collection<UserDto> getFriends(@PathVariable long id) {
        log.debug("Получен запрос на получение друзей пользователя с id: {}", id);
        Collection<UserDto> friends = userService.getFriends(id);
        log.info("Получено {} друзей пользователя {}", friends.size(), id);
        return friends;
    }

    //Удалить друга
    @DeleteMapping("/{id}/friends/{friendId}")
    public UserDto deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("Пользователь {} удаляет из друзей пользователя {}", id, friendId);
        UserDto user = userService.deleteFriend(id, friendId);
        log.info("Пользователь {} успешно удален из друзей пользователя {}", friendId, id);
        return user;
    }

    //Получить список общих друзей
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.debug("Получен запрос на получение общих друзей пользователей {} и {}", id, otherId);
        Collection<UserDto> commonFriends = userService.getCommonFriend(id, otherId);
        log.info("Получено {} общих друзей пользователей {} и {}", commonFriends.size(), id, otherId);
        return commonFriends;
    }

}
