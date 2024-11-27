package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.error.exception.DuplicateEmailException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getShouldGetUserDtoWithAvailableId() {
        Long userId = 1L;
        User user = new User();
        UserDto userDto = new UserDto();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        userService.get(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(modelMapper, times(1)).map(user, UserDto.class);
    }

    @Test
    void getShouldThrowNotFoundExceptionWhenUserIdIsNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.get(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with id " + userId + " not found");

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void saveShouldSaveUserRequest() {
        User user = new User();
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("someemail@mail.ru");
        UserRequest userRequest = new UserRequest();
        when(modelMapper.map(userRequest, User.class)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto savedUser = userService.save(userRequest);

        assertThat(userDto).isEqualTo(savedUser);
        verify(modelMapper, times(1)).map(userRequest, User.class);
        verify(userRepository, times(1)).save(user);
        verify(modelMapper, times(1)).map(user, UserDto.class);
    }

    @Test
    void saveShouldDuplicateEmailExceptionWhenUserEmailIsAlreadyExists() {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("someemail@mail.ru");
        User user = new User();
        when(modelMapper.map(userRequest, User.class)).thenReturn(user);
        when(userRepository.save(user)).thenThrow(new DataIntegrityViolationException(""));

        assertThatThrownBy(() -> userService.save(userRequest))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("Email " + userRequest.getEmail() + " already exists");

        verify(modelMapper, times(1)).map(userRequest, User.class);
        verifyNoMoreInteractions(modelMapper);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateShouldUpdateUserFields() {
        Long userId = 1L;
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setName("someName");
        userUpdateRequest.setEmail("someEmail");
        User user = new User();
        UserDto userDto = new UserDto();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        userService.update(userId, userUpdateRequest);

        assertThat(user.getName()).isEqualTo(userUpdateRequest.getName());
        assertThat(user.getEmail()).isEqualTo(userUpdateRequest.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
        verify(modelMapper, times(1)).map(user, UserDto.class);
    }

    @Test
    void updateShouldNotUpdateUserWithEmptyUserUpdateRequest() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        User user = new User();
        user.setName("someName");
        user.setEmail("someEmail");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        userService.update(userId, new UserUpdateRequest());

        assertThat(user.getName()).isEqualTo("someName");
        assertThat(user.getEmail()).isEqualTo("someEmail");
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
        verify(modelMapper, times(1)).map(user, UserDto.class);
    }

    @Test
    void updateShouldThrowNotFoundExceptionWhenUserIdIsNotFound() {
        Long userId = 1L;
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(userId, userUpdateRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with id " + userId + " not found");

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void deleteShouldDeleteUserById() {
        userService.delete(anyLong());

        verify(userRepository, times(1)).deleteById(anyLong());
    }
}