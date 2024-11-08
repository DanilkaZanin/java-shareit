package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.user.UserEmailConfiguration.email;

@SpringBootTest
class UserControllerTests {
    @Autowired
    UserController userController;

    private UserRequest getUser() {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail(email());
        userRequest.setName("test");
        return userRequest;
    }

    @Test
    void shouldAddUser() {
        UserRequest userRequest = getUser();
        UserDto userDto = userController.saveUser(userRequest);

        assertNotNull(userDto);
        assertEquals(userRequest.getName(), userDto.getName());
        assertEquals(userRequest.getEmail(), userDto.getEmail());
    }

    @Test
    void shouldUpdateUser() {
        UserRequest userRequest = getUser();
        UserDto userDto = userController.saveUser(userRequest);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setEmail(userDto.getEmail());
        userUpdateRequest.setName("new name");
        UserDto updatedUserDto = userController.updateUser(userDto.getId(), userUpdateRequest);

        assertNotNull(updatedUserDto);
        assertEquals("new name", updatedUserDto.getName());
    }

    @Test
    void shouldGetUser() {
        UserRequest userRequest = getUser();
        UserDto userDto = userController.saveUser(userRequest);

        UserDto getUserDto = userController.getUser(userDto.getId());

        assertNotNull(getUserDto);
        assertEquals(userDto, getUserDto);
    }

    @Test
    void shouldDeleteUser() {
        UserRequest userRequest = getUser();
        UserDto userDto = userController.saveUser(userRequest);

        userController.deleteUser(userDto.getId());

        assertThrows(NotFoundException.class, () -> userController.getUser(userDto.getId()));
    }
}