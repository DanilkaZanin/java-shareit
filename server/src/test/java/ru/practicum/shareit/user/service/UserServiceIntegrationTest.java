package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.DuplicateEmailException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserServiceIntegrationTest {

    private final UserService userService;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        userRepository.save(user);
    }

    @Test
    void shouldGetUserById() {
        UserDto userDto = userService.get(user.getId());

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void shouldSaveNewUser() {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("New User");
        userRequest.setEmail("newuser@example.com");

        UserDto savedUserDto = userService.save(userRequest);

        assertNotNull(savedUserDto);
        assertNotNull(savedUserDto.getId());
        assertEquals(userRequest.getName(), savedUserDto.getName());
        assertEquals(userRequest.getEmail(), savedUserDto.getEmail());
    }

    @Test
    void shouldThrowDuplicateEmailExceptionWhenSavingUserWithExistingEmail() {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("Another User");
        userRequest.setEmail("testuser@example.com");  // тот же email, что и у существующего пользователя

        assertThrows(DuplicateEmailException.class, () -> userService.save(userRequest));
    }

    @Test
    void shouldUpdateUser() {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setName("Updated User");
        userUpdateRequest.setEmail("updateduser@example.com");

        UserDto updatedUserDto = userService.update(user.getId(), userUpdateRequest);

        assertNotNull(updatedUserDto);
        assertEquals("Updated User", updatedUserDto.getName());
        assertEquals("updateduser@example.com", updatedUserDto.getEmail());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdatingNonExistingUser() {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setName("Non Existing User");

        assertThrows(NotFoundException.class, () -> userService.update(999L, userUpdateRequest)); // 999L - несуществующий ID
    }

    @Test
    void shouldDeleteUser() {
        Long userId = user.getId();

        userService.delete(userId);

        assertFalse(userRepository.existsById(userId)); // Проверяем, что пользователь был удален из базы
    }
}