package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
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
        return userService.addUser(request);
    }

    //Получить список всех пользователей
    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    //Обновить данные пользователя
    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(request);
    }


    //Удалить пользователя
    @DeleteMapping("/{id}")
    public UserDto deleteUser(@PathVariable long id) {
        return userService.deleteUser(id);
    }

    //Получить пользователя по id
    @GetMapping("/{id}")
    public UserDto findById(@PathVariable long id) {
        return userService.findById(id);
    }

    //Добавить друга
    @PutMapping("/{id}/friends/{friendId}")
    public UserDto addFriend(@PathVariable long id, @PathVariable long friendId) {
        return userService.addFriend(id, friendId);
    }

    //Получить список друзей пользователя
    @GetMapping("/{id}/friends")
    public Collection<UserDto> getFriends(@PathVariable long id) {
        return userService.getFriends(id);
    }

    //Удалить друга
    @DeleteMapping("/{id}/friends/{friendId}")
    public UserDto deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        return userService.deleteFriend(id, friendId);
    }

    //Получить список общих друзей
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriend(id, otherId);
    }

}
