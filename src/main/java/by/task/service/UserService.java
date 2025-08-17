package by.task.services;

import by.task.dao.Dao;
import by.task.dao.impl.UserDao;
import by.task.exception.services.EmptyUserListException;
import by.task.exception.services.InvalidUserException;
import by.task.exception.services.UserNotFoundException;
import by.task.models.User;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final Dao<User, Long> userDao;

    public UserService() {
        this.userDao = new UserDao();
    }

    public UserService(Dao<User, Long> userDao) {
        this.userDao = userDao;
    }

    public void saveUser(User user) {
        validateUser(user);
        userDao.save(user);
    }

    public Optional<User> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidUserException("Некорректный ID пользователя");
        }
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            throw new EmptyUserListException();
        }
        return users;
    }

    public void updateUser(User user) {
        validateUser(user);
        if (userNotExist(user.getId())) {
            throw new UserNotFoundException(user.getId());
        }
        userDao.update(user);
    }

    public void deleteUser(User user) {
        if (userNotExist(user.getId())) {
            throw new UserNotFoundException(user.getId());
        }
        userDao.delete(user);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new InvalidUserException("Пользователь не может быть null");
        }
        if (StringUtils.isBlank(user.getName())) {
            throw new InvalidUserException("Имя пользователя обязательно");
        }
    }

    private boolean userNotExist(Long id) {
        return id == null || userDao.findById(id).isEmpty();
    }
}