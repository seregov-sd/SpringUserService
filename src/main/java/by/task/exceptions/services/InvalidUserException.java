package by.task.exceptions.services;

public class InvalidUserException extends UserServiceException {
    public InvalidUserException(String message) {
        super(message);
    }
}