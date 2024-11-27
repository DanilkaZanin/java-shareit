package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithoutResponses;
import ru.practicum.shareit.request.dto.ItemRequestRequset;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoWithoutResponses addRequest(ItemRequestRequset requset, Long userId);

    List<ItemRequestDto> getMyRequests(Long userId);

    List<ItemRequestDtoWithoutResponses> getAllRequests(Long userId);

    ItemRequestDto getRequest(Long requestId, Long userId);
}