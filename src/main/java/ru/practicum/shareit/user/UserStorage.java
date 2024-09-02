package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserStorage {
    Optional<User> get(long id);

    User create(User user);

    User update(User user);

    void delete(long id);

    boolean checkEmail(String email);
}