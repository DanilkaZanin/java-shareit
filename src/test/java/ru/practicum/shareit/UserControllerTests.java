package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.UserEmailConfiguration.email;

@SpringBootTest
public class UserControllerTests {
    @Autowired
    UserController userController;

    private User getUser() {
        User user = new User();
        user.setEmail(email());
        user.setName("test");
        return user;
    }

    @Test
    public void shouldAddUser() {
        User user = getUser();
        UserDto userDto = userController.saveUser(user);

        assertNotNull(userDto);
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    public void shouldUpdateUser() {
        User user = getUser();
        UserDto userDto = userController.saveUser(user);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setEmail(userDto.getEmail());
        userUpdateRequest.setName("new name");
        UserDto updatedUserDto = userController.updateUser(userDto.getId(), userUpdateRequest);

        assertNotNull(updatedUserDto);
        assertEquals("new name", updatedUserDto.getName());
    }

    @Test
    public void shouldGetUser() {
        User user = getUser();
        UserDto userDto = userController.saveUser(user);

        UserDto getUserDto = userController.getUser(user.getId());

        assertNotNull(getUserDto);
        assertEquals(userDto, getUserDto);
    }

    @Test
    public void shouldDeleteUser() {
        User user = getUser();
        UserDto userDto = userController.saveUser(user);

        userController.deleteUser(user.getId());

        assertThrows(NotFoundException.class, () -> userController.getUser(userDto.getId()));
    }
}