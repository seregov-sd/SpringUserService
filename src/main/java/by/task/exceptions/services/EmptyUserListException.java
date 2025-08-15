package by.task.exceptions.services;

public class EmptyUserListException extends UserServiceException {
    public EmptyUserListException() {
        super("В системе пока нет пользователей");
    }
}