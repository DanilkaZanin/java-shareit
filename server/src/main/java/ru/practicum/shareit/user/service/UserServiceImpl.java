package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.DuplicateEmailException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public UserDto get(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto save(UserRequest userRequest) {
        try {
            User user = modelMapper.map(userRequest, User.class);
            User savedUser = userRepository.save(user);
            return modelMapper.map(savedUser, UserDto.class);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Email " + userRequest.getEmail() + " already exists");
        }
    }

    @Override
    public UserDto update(Long id, UserUpdateRequest userUpdateRequest) {
        return userRepository.findById(id)
                .map(user -> {
                    updateUserFields(user, userUpdateRequest);
                    return modelMapper.map(userRepository.save(user), UserDto.class);
                })
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    private void updateUserFields(User user, UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest.getName() != null) {
            user.setName(userUpdateRequest.getName());
        }
        if (userUpdateRequest.getEmail() != null) {
            user.setEmail(userUpdateRequest.getEmail());
        }
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}