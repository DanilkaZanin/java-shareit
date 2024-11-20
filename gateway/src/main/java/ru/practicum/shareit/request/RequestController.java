package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequset;

@Controller
@Slf4j
@RequestMapping(value = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@Valid @RequestBody ItemRequestRequset request,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Add new request with user id {} and description {}", userId, request.getDescription());
        return requestClient.addRequest(request, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getMyRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get owner requests with user id {}", userId);
        return requestClient.getMyRequests(userId);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Object> getRequestsAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get all requests with user id {}", userId);
        return requestClient.getAllRequests(userId);
    }

    @GetMapping(path = "{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable("requestId") long requestId,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get request with user id {} and request id {}", userId, requestId);
        return requestClient.getRequest(requestId, userId);
    }
}