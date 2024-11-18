package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithoutResponses;
import ru.practicum.shareit.request.dto.ItemRequestRequset;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoWithoutResponses addRequest(@Valid @RequestBody ItemRequestRequset request,
                                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.addRequest(request, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getMyRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getMyRequests(userId);
    }

    @GetMapping(path = "all")
    public List<ItemRequestDtoWithoutResponses> getRequestsAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping(path = "{requestId}")
    public ItemRequestDto getRequest(@PathVariable("requestId") long requestId,
                                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getRequest(requestId, userId);
    }
}