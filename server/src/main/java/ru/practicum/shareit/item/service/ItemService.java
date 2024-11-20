package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;

import java.util.List;

public interface ItemService {
    ItemDto save(Long ownerId, ItemRequest itemRequest);

    ItemDto update(Long ownerId, Long itemId, ItemUpdateRequest item);

    List<ItemDto> search(Long ownerId, String text);

    ItemDto get(Long ownerId, Long itemId);

    List<ItemDto> getAllItemsFromOwner(Long ownerId);

    CommentDto saveComment(Long bookerId, Long itemId, CommentRequest commentRequest);

    List<ItemResponseDto> getItemsByRequestId(Long requestId);
}