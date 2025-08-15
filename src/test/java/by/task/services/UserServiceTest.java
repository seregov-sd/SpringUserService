package by.task.services;

import by.task.dao.Dao;
import by.task.exceptions.services.EmptyUserListException;
import by.task.exceptions.services.InvalidUserException;
import by.task.exceptions.services.UserNotFoundException;
import by.task.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private Dao<User, Long> userDao;

    @InjectMocks
    private UserService userService;

    @Test
    void saveUser_validUser_savesSuccessfully() {
        User user = createValidUser();
        userService.saveUser(user);
        verify(userDao).save(user);
    }

    @Test
    void saveUser_nullUser_throwsInvalidUserException() {
        assertThrows(InvalidUserException.class, () -> userService.saveUser(null));
        verifyNoInteractions(userDao);
    }

    @Test
    void saveUser_blankName_throwsInvalidUserException() {
        User user = createValidUser();
        user.setName("");
        assertThrows(InvalidUserException.class, () -> userService.saveUser(user));
        verifyNoInteractions(userDao);
    }

    @Test
    void getUserById_validId_returnsUser() {
        Long id = 1L;
        User expectedUser = createValidUser();
        when(userDao.findById(id)).thenReturn(Optional.of(expectedUser));

        Optional<User> result = userService.getUserById(id);
        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
    }

    @Test
    void getUserById_invalidId_throwsInvalidUserException() {
        assertThrows(InvalidUserException.class, () -> userService.getUserById(0L));
        verifyNoInteractions(userDao);
    }

    @Test
    void getUserById_nonExistentId_returnsEmptyOptional() {
        Long id = 999L;
        when(userDao.findById(id)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(id);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllUsers_nonEmptyList_returnsUsers() {
        List<User> users = List.of(createValidUser());
        when(userDao.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllUsers_emptyList_throwsEmptyUserListException() {
        when(userDao.findAll()).thenReturn(Collections.emptyList());
        assertThrows(EmptyUserListException.class, () -> userService.getAllUsers());
    }

    @Test
    void updateUser_validUser_updatesSuccessfully() {
        User user = createValidUser();
        user.setId(1L);
        when(userDao.findById(user.getId())).thenReturn(Optional.of(user));

        userService.updateUser(user);
        verify(userDao).update(user);
    }

    @Test
    void updateUser_nonExistentUser_throwsUserNotFoundException() {
        User user = createValidUser();
        user.setId(999L);
        when(userDao.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(user));
        verify(userDao, never()).update(any());
    }

    @Test
    void deleteUser_validUser_deletesSuccessfully() {
        User user = createValidUser();
        user.setId(1L);
        when(userDao.findById(user.getId())).thenReturn(Optional.of(user));

        userService.deleteUser(user);
        verify(userDao).delete(user);
    }

    @Test
    void deleteUser_nonExistentUser_throwsUserNotFoundException() {
        User user = createValidUser();
        user.setId(999L);
        when(userDao.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(user));
        verify(userDao, never()).delete(any());
    }

    private User createValidUser() {
        return new User("Valid Name", "valid@email.com", 30);
    }
}