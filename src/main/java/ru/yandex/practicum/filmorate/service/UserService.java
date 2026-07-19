package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.storage.user.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.storage.user.dto.UserDto;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserDbStorage userStorage;

    //Добавить пользователя
    public UserDto addUser(NewUserRequest request) {
        User user = UserMapper.mapToUser(request);
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Login не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return UserMapper.mapToUserDto(userStorage.addUser(user));
    }

    //Получить всех пользователей
    public Collection<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    //Получить пользователя по id
    public UserDto findById(long id) {
        return UserMapper.mapToUserDto(userStorage.findById(id));
    }

    //Обновить данные пользователя
    public UserDto updateUser(UpdateUserRequest request) {
        User user = userStorage.findById(request.getId());

        User userUpdate = UserMapper.mapToUpdate(user, request);
        return UserMapper.mapToUserDto(userStorage.updateUser(userUpdate));
    }

    //Удалить пользователя
    public UserDto deleteUser(long id) {
        return UserMapper.mapToUserDto(userStorage.deleteUser(id));
    }


    //Добавить друга
    public UserDto addFriend(long id, long friendId) {


        try {
            userStorage.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        try {
            userStorage.findById(friendId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Друг с id = " + friendId + " не найден");
        }

        if (id == friendId) {
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }


        if (userStorage.getFriends(id).stream()
                .anyMatch(user -> user.equals(userStorage.findById(id)))) {
            throw new ValidationException("Пользователь уже у вас в друзьях");
        }

        if (userStorage.getFriends(friendId).stream()
                .anyMatch(user -> user.getId() == id)) {
            userStorage.updateFriendshipIsConfirmed(friendId, id, true);
            return UserMapper.mapToUserDto(userStorage.addFriend(id, friendId, true));
        } else {
            return UserMapper.mapToUserDto(userStorage.addFriend(id, friendId, false));
        }
    }

    //Получить список друзей пользователя
    public Collection<UserDto> getFriends(long id) {
        try {
            userStorage.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        return userStorage.getFriends(id).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    //Удалить друга
    public UserDto deleteFriend(long id, long friendId) {
        User friend = userStorage.findById(friendId);

        try {
            userStorage.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        try {
            userStorage.findById(friendId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Друг с id = %d не найден", friendId));
        }

        userStorage.deleteFriend(id, friendId);
        userStorage.updateFriendshipIsConfirmed(friendId, id, false);

        return UserMapper.mapToUserDto(friend);
    }

    //Получить список общих друзей
    public Collection<UserDto> getCommonFriend(long id, long otherId) {
        try {
            userStorage.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));

        }
        try {
            userStorage.findById(otherId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Другой пользователь с id = %d не найден", otherId));
        }

        return userStorage.getCommonFriends(id, otherId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

}
