package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    ItemDto create(Item itemDto);

    ItemDto update(long ownerId, long itemId, ItemUpdateRequest itemDto);

    List<ItemDto> search(String text);

    Optional<ItemDto> get(long id);

    List<ItemDto> getAllItemsFromOwner(long ownerId);
}