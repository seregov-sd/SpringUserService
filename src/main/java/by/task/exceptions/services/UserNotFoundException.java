package by.task.exceptions.services;

public class UserNotFoundException extends UserServiceException {
    public UserNotFoundException(Long id) {
        super("Пользователь с ID " + id + " не найден");
    }
}