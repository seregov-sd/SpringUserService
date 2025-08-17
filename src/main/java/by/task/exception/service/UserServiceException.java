package by.task.exceptions.services;

public class UserServiceException extends RuntimeException {
    public UserServiceException(String message) {
        super(message);
    }
}