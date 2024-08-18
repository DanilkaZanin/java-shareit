package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userRepository;

    public UserDto get(long id) {
        if (userRepository.get(id).isEmpty()) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        return userRepository.get(id).get();
    }

    public UserDto save(User user) {
        if (userRepository.checkEmail(user.getEmail())) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }
        return userRepository.create(user);
    }

    public UserDto update(long id, UserUpdateRequest user) {
        Optional<UserDto> userOptional = userRepository.get(id);

        if (userOptional.isEmpty()) {
            throw new NotFoundException("User with id " + id + " not found");
        }

        if (user.getEmail() != null && !Objects.equals(user.getEmail(), userOptional.get().getEmail())) {
            if ((userRepository.checkEmail(user.getEmail()))) {
                throw new RuntimeException("User with email " + user.getEmail() + " already exists");
            }
        }
        return userRepository.update(id, user);
    }

    public void delete(long id) {
        if (userRepository.get(id).isEmpty()) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        userRepository.delete(id);
    }
}