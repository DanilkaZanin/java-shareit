package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    public UserDto get(long id) {
        if (userRepository.get(id).isEmpty()) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        return modelMapper.map(userRepository.get(id).get(), UserDto.class);
    }

    public UserDto save(ru.practicum.shareit.user.dto.UserRequest userRequest) {
        if (userRepository.checkEmail(userRequest.getEmail())) {
            throw new RuntimeException("User with email " + userRequest.getEmail() + " already exists");
        }
        User user = modelMapper.map(userRequest, User.class);
        return modelMapper.map(userRepository.create(user), UserDto.class);
    }

    public UserDto update(long id, UserUpdateRequest userUpdateRequest) {
        Optional<User> userOptional = userRepository.get(id);

        if (userOptional.isEmpty()) {
            throw new NotFoundException("User with id " + id + " not found");
        }

        if (userUpdateRequest.getEmail() != null && !Objects.equals(userUpdateRequest.getEmail(), userOptional.get().getEmail())) {
            if ((userRepository.checkEmail(userUpdateRequest.getEmail()))) {
                throw new RuntimeException("User with email " + userUpdateRequest.getEmail() + " already exists");
            }
        }
        User user = userOptional.get();
        modelMapper.map(userUpdateRequest, user);
        return modelMapper.map(userRepository.update(user), UserDto.class);
    }

    public void delete(long id) {
        if (userRepository.get(id).isEmpty()) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        userRepository.delete(id);
    }
}