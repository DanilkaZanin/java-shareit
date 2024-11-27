package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithoutResponses;
import ru.practicum.shareit.request.dto.ItemRequestRequset;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemService itemService;
    private final ItemRequestRepository itemRequestRepository;
    private final ModelMapper modelMapper;

    @Override
    public ItemRequestDtoWithoutResponses addRequest(ItemRequestRequset request, Long userId) {
        ItemRequest itemRequest = modelMapper.map(request, ItemRequest.class);
        itemRequest.setRequestorId(userId);
        itemRequest.setCreated(LocalDateTime.now());
        return modelMapper.map(itemRequestRepository.save(itemRequest), ItemRequestDtoWithoutResponses.class);
    }

    @Override
    public ItemRequestDto getRequest(Long requestId, Long userId) {
        return itemRequestRepository.findById(requestId)
                .map(itemRequest -> {
                    ItemRequestDto dto = modelMapper.map(itemRequest, ItemRequestDto.class);
                    dto.setItems(itemService.getItemsByRequestId(requestId));
                    return dto;
                })
                .orElseThrow(() -> new NotFoundException(String.format("request with id %d not found!", requestId)));

    }

    @Override
    public List<ItemRequestDto> getMyRequests(Long userId) {
        List<ItemRequestDto> itemRequestDtos =
                itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId).stream()
                        .map(itemRequest -> modelMapper.map(itemRequest, ItemRequestDto.class))
                        .toList();

        itemRequestDtos.forEach(itemRequestDto -> itemRequestDto.setItems(itemService.getItemsByRequestId(itemRequestDto.getId())));
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDtoWithoutResponses> getAllRequests(Long userId) {
        return itemRequestRepository.findAllNotOwnerRequestsSortedByCreatedTimeDesc(userId).stream()
                .map(itemRequest -> modelMapper.map(itemRequest, ItemRequestDtoWithoutResponses.class))
                .toList();
    }
}