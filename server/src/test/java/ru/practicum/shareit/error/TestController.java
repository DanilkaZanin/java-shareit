package ru.practicum.shareit.error;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.UnauthorizedException;

@RestController
public class TestController {

    @GetMapping("/not-found")
    public void throwNotFoundException() {
        throw new NotFoundException("Not Found Error");
    }

    @GetMapping("/internal-error")
    public void throwInternalErrorException() {
        throw new RuntimeException("Unexpected Error");
    }

    @GetMapping("/forbidden")
    public void throwForbiddenException() {
        throw new UnauthorizedException("Unauthorized Access");
    }
}