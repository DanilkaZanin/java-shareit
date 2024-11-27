package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

public interface UserService {
    UserDto get(Long id);

    UserDto save(UserRequest userRequest);

    UserDto update(Long id, UserUpdateRequest userUpdateRequest);

    void delete(Long id);
}