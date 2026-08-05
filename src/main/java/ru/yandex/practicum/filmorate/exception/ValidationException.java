package ru.yandex.practicum.filmorate.exception;

public class ValidationException extends RuntimeException {
    //Конструктор исключения для ошибок валидации
    public ValidationException(String message) {
        super(message);
    }
}
