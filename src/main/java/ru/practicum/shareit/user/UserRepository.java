package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserRepository implements UserStorage {
    private final Map<Long, User> users;
    private final ModelMapper modelMapper;
    private long customIdCounter;

    private long nextId() {
        return customIdCounter++;
    }

    @Override
    public Optional<UserDto> get(long id) {
        return Optional.ofNullable(users.get(id)).map(u -> modelMapper.map(u, UserDto.class));
    }

    @Override
    public UserDto create(User user) {
        user.setId(nextId());
        users.put(user.getId(), user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto update(long id, UserUpdateRequest userUpdateRequest) {
        User userToUpdate = users.get(id);
        modelMapper.getConfiguration().setPropertyCondition(new NotNullCondition());
        modelMapper.map(userUpdateRequest, userToUpdate);

        users.put(id, userToUpdate);
        return modelMapper.map(userToUpdate, UserDto.class);
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    @Override
    public boolean checkEmail(String email) {
        return users.values().stream().anyMatch(u -> u.getEmail().equals(email));
    }
}