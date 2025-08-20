package by.task.service;

import by.task.dto.UserRequestDTO;
import by.task.dto.UserResponseDTO;
import by.task.exception.service.EmptyUserListException;
import by.task.exception.service.InvalidUserException;
import by.task.exception.service.UserNotFoundException;
import by.task.mapper.UserMapper;
import by.task.model.User;
import by.task.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long NON_EXISTING_ID = 999L;
    private static final String TEST_NAME = "Test User";
    private static final String ALT_NAME = "Alt User";
    private static final String TEST_EMAIL = "user@test.com";
    private static final int TEST_AGE = 30;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_ValidUser_ReturnsUserResponseDTO() {
        UserRequestDTO requestDTO = createValidRequestDTO();
        User userEntity = createValidUserWithoutId(); // Создаем ВАЛИДНОГО пользователя
        User savedUser = createTestUser(USER_ID);
        UserResponseDTO expectedResponse = createTestResponseDTO(USER_ID);

        when(userMapper.toEntity(requestDTO)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(savedUser);
        when(userMapper.toDTO(savedUser)).thenReturn(expectedResponse);

        UserResponseDTO result = userService.createUser(requestDTO);

        assertSame(expectedResponse, result);
        verify(userMapper).toEntity(requestDTO);
        verify(userRepository).save(userEntity);
        verify(userMapper).toDTO(savedUser);
    }

    @Test
    void createUser_InvalidName_ThrowsException() {
        UserRequestDTO invalidRequest = createValidRequestDTO();
        invalidRequest.setName(" ");

        User invalidUser = new User();
        invalidUser.setName(" "); // Пустое имя

        when(userMapper.toEntity(invalidRequest)).thenReturn(invalidUser);

        assertThrows(InvalidUserException.class, () -> userService.createUser(invalidRequest));
        verify(userMapper).toEntity(invalidRequest);
        verifyNoInteractions(userRepository);
    }

    @Test
    void getUserById_ExistingUser_ReturnsUserResponseDTO() {
        Long userId = USER_ID;
        User user = createTestUser(userId);
        UserResponseDTO expectedResponse = createTestResponseDTO(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(expectedResponse);

        UserResponseDTO result = userService.getUserById(userId);

        assertSame(expectedResponse, result);
        verify(userRepository).findById(userId);
        verify(userMapper).toDTO(user);
    }

    @Test
    void getUserById_NonExistingUser_ThrowsException() {
        Long nonExistingId = NON_EXISTING_ID;

        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(nonExistingId));
        verify(userRepository).findById(nonExistingId);
        verifyNoInteractions(userMapper);
    }

    @Test
    void getAllUsers_WithUsers_ReturnsList() {
        User user = createTestUser(USER_ID);
        UserResponseDTO expectedResponse = createTestResponseDTO(USER_ID);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDTO(user)).thenReturn(expectedResponse);

        List<UserResponseDTO> result = userService.getAllUsers();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertSame(expectedResponse, result.get(0));
        verify(userRepository).findAll();
        verify(userMapper).toDTO(user);
    }

    @Test
    void getAllUsers_EmptyList_ThrowsException() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(EmptyUserListException.class, () -> userService.getAllUsers());
        verify(userRepository).findAll();
        verifyNoInteractions(userMapper);
    }

    @Test
    void updateUser_ValidUser_UpdatesSuccessfully() {
        Long userId = USER_ID;
        User existingUser = createTestUser(userId);
        existingUser.setName(TEST_NAME);

        UserRequestDTO updateDTO = createValidRequestDTO();
        updateDTO.setName(ALT_NAME);

        User updatedUser = createTestUser(userId);
        updatedUser.setName(ALT_NAME);

        UserResponseDTO expectedResponse = createTestResponseDTO(userId);
        expectedResponse.setName(ALT_NAME);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toDTO(updatedUser)).thenReturn(expectedResponse);

        UserResponseDTO result = userService.updateUser(userId, updateDTO);

        assertEquals(ALT_NAME, result.getName());
        verify(userRepository).findById(userId);
        verify(userMapper).updateEntityFromDTO(updateDTO, existingUser);
        verify(userRepository).save(existingUser);
        verify(userMapper).toDTO(updatedUser);
    }

    @Test
    void deleteUser_ExistingUser_DeletesSuccessfully() {
        Long userId = USER_ID;

        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
        verifyNoInteractions(userMapper);
    }

    @Test
    void deleteUser_NonExistingUser_ThrowsException() {
        Long nonExistingId = NON_EXISTING_ID;

        when(userRepository.existsById(nonExistingId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(nonExistingId));
        verify(userRepository).existsById(nonExistingId);
        verify(userRepository, never()).deleteById(any());
        verifyNoInteractions(userMapper);
    }

    private User createValidUserWithoutId() {
        User user = new User();
        user.setName(TEST_NAME); // Устанавливаем имя
        user.setEmail(TEST_EMAIL);
        user.setAge(TEST_AGE);
        return user;
    }

    private UserRequestDTO createValidRequestDTO() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setName(TEST_NAME);
        dto.setEmail(TEST_EMAIL);
        dto.setAge(TEST_AGE);
        return dto;
    }

    private User createTestUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setName(TEST_NAME);
        user.setEmail(TEST_EMAIL);
        user.setAge(TEST_AGE);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    private UserResponseDTO createTestResponseDTO(Long id) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(id);
        dto.setName(TEST_NAME);
        dto.setEmail(TEST_EMAIL);
        dto.setAge(TEST_AGE);
        dto.setCreatedAt(LocalDateTime.now());
        return dto;
    }
}