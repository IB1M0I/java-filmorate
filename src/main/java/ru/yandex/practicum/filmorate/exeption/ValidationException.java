package ru.yandex.practicum.filmorate.exeption;

public class ValidationException extends RuntimeException {
    //Конструктор исключения для ошибок валидации
    public ValidationException(String message) {
        super(message);
    }
}
