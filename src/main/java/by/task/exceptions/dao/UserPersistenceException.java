package by.task.exceptions.dao;

public class UserPersistenceException extends DaoException {
    public UserPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}