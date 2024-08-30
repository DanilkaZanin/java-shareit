package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserStorage {
    Optional<UserDto> get(long id);

    UserDto create(User user);

    UserDto update(long id, UserUpdateRequest user);

    void delete(long id);

    boolean checkEmail(String email);
}
