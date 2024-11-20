package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.info("Get user with id {}", userId);
        return userClient.getUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> createUser(@Validated @RequestBody UserRequest userRequest) {
        log.info("Create user with mail {} and name {}", userRequest.getEmail(), userRequest.getName());
        return userClient.createUser(userRequest);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateUser(@PathVariable long userId, @Validated @RequestBody UserUpdateRequest user) {
        log.info("Update user with id {}, new name {}, new email {}", userId, user.getName(), user.getEmail());
        return userClient.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        log.info("Delete user with id {}", userId);
        return userClient.deleteUser(userId);
    }
}