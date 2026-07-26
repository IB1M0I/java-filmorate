package ru.yandex.practicum.filmorate.exeption;

public class NotFoundException extends RuntimeException {
    //Конструктор исключения для случая когда ресурс не найден
    public NotFoundException(String message) {
        super(message);
    }
}
