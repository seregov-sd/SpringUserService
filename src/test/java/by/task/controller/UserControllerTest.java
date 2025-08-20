package by.task.controller;

import by.task.dto.UserRequestDTO;
import by.task.dto.UserResponseDTO;
import by.task.exception.service.EmptyUserListException;
import by.task.exception.service.UserNotFoundException;
import by.task.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "Test User";
    private static final String USER_EMAIL = "test@example.com";
    private static final int USER_AGE = 30;

    @Test
    void createUser_ValidData_ReturnsCreated() throws Exception {
        UserRequestDTO requestDTO = new UserRequestDTO(USER_NAME, USER_EMAIL, USER_AGE);
        UserResponseDTO responseDTO = new UserResponseDTO(
                USER_ID, USER_NAME, USER_EMAIL, USER_AGE, LocalDateTime.now()
        );

        given(userService.createUser(any(UserRequestDTO.class))).willReturn(responseDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value(USER_NAME))
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.age").value(USER_AGE));
    }

    @Test
    void createUser_InvalidData_ReturnsBadRequest() throws Exception {
        UserRequestDTO invalidDTO = new UserRequestDTO(" ", "invalid-email", -1);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"))
                .andExpect(jsonPath("$.email").value("Invalid email format"))
                .andExpect(jsonPath("$.age").value("Age must be positive"));
    }

    @Test
    void getUserById_ExistingUser_ReturnsOk() throws Exception {
        UserResponseDTO responseDTO = new UserResponseDTO(USER_ID, USER_NAME, USER_EMAIL, USER_AGE, LocalDateTime.now());

        given(userService.getUserById(USER_ID)).willReturn(responseDTO);

        mockMvc.perform(get("/api/users/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value(USER_NAME));
    }

    @Test
    void getUserById_NonExistingUser_ReturnsNotFound() throws Exception {
        given(userService.getUserById(anyLong()))
                .willThrow(new UserNotFoundException(USER_ID));

        mockMvc.perform(get("/api/users/{id}", USER_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Пользователь с ID " + USER_ID + " не найден"));
    }

    @Test
    void getAllUsers_WithUsers_ReturnsOk() throws Exception {
        UserResponseDTO user = new UserResponseDTO(USER_ID, USER_NAME, USER_EMAIL, USER_AGE, LocalDateTime.now());
        List<UserResponseDTO> users = List.of(user);

        given(userService.getAllUsers()).willReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(USER_ID))
                .andExpect(jsonPath("$[0].name").value(USER_NAME))
                .andExpect(jsonPath("$[0].email").value(USER_EMAIL));
    }

    @Test
    void getAllUsers_EmptyList_ReturnsNotFound() throws Exception {
        given(userService.getAllUsers())
                .willThrow(new EmptyUserListException());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("В системе пока нет пользователей"));
    }

    @Test
    void updateUser_ValidData_ReturnsOk() throws Exception {
        UserRequestDTO requestDTO = new UserRequestDTO("Updated Name", "updated@test.com", USER_AGE);
        UserResponseDTO responseDTO = new UserResponseDTO(
                USER_ID, "Updated Name", "updated@test.com", USER_AGE, LocalDateTime.now()
        );

        given(userService.updateUser(eq(USER_ID), any(UserRequestDTO.class))).willReturn(responseDTO);

        mockMvc.perform(put("/api/users/{id}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@test.com"))
                .andExpect(jsonPath("$.age").value(USER_AGE));
    }

    @Test
    void deleteUser_ExistingUser_ReturnsNoContent() throws Exception {
        doNothing().when(userService).deleteUser(USER_ID);

        mockMvc.perform(delete("/api/users/{id}", USER_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_NonExistingUser_ReturnsNotFound() throws Exception {
        doThrow(new UserNotFoundException(USER_ID))
                .when(userService).deleteUser(USER_ID);

        mockMvc.perform(delete("/api/users/{id}", USER_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь с ID " + USER_ID + " не найден"));
    }

    @Test
    void createUser_ValidationFailed_ReturnsBadRequest() throws Exception {
        UserRequestDTO invalidDTO = new UserRequestDTO(USER_NAME, USER_EMAIL, -1);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.age").value("Age must be positive"));
    }
}