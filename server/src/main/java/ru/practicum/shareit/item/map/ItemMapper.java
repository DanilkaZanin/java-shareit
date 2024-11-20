package ru.practicum.shareit.item.map;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemMapper {
    ItemDto toDto(Item item);
}
