package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    //Добавить друга
    public User addFriend(long id, long friendId) {
        User user = userStorage.findById(id);
        User friend = userStorage.findById(friendId);

        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        if (friend == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", friendId));
        }
        if(id == friendId){
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }



        if(user.getFriends().contains(friendId)){
            throw new ValidationException("Пользователь с id = " + friendId + " уже добавлен в друзья");
        }

        user.getFriends().add(friendId);
        return user;
    }

    //Удалить друга
    public User deleteFriend(long id, long friendId) {
        User user = userStorage.findById(id);
        User friend = userStorage.findById(friendId);

        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        if (friend == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", friendId));
        }
        if(!user.getFriends().contains(friendId)){
            throw new ValidationException("Пользователь с id = " + friendId + " не добавлен в друзья");
        }

        user.getFriends().remove(friendId);

        return user;
    }

    //Получить список друзей пользователя
    public Collection<User> getFriends(long id) {
        if (userStorage.findById(id) == null) {
            throw new NotFoundException("Пользователь с id = %d не найден");
        }
        User user = userStorage.findById(id);
        return user.getFriends().stream()
                .map(userStorage::findById)
                .toList();
    }

    //Получить список общих друзей
    public Collection<User> getCommonFriend(long id, long otherId) {
        if (userStorage.findById(id) == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        if (userStorage.findById(otherId) == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", otherId));
        }

        User user = userStorage.findById(id);
        User otherUser = userStorage.findById(otherId);
        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(userStorage::findById)
                .toList();
    }

    //Добавить пользователя
    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    //Обновить данные пользователя
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    //Удалить пользователя
    public User deleteUser(long id) {
        return userStorage.deleteUser(id);
    }

    //Получить всех пользователей
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    //Получить пользователя по id
    public User findById(long id) {
        return userStorage.findById(id);
    }
}
