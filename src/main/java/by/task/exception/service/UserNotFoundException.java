package by.task.exception.service;

public class UserNotFoundException extends UserServiceException {
    public UserNotFoundException(Long id) {
        super("Пользователь с ID " + id + " не найден");
    }

    public UserNotFoundException(Long id, Throwable cause) {
        super("Пользователь с ID " + id + " не найден", cause);
    }
}