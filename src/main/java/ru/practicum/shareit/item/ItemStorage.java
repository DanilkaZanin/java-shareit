package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item create(Item item);

    Item update(long ownerId, long itemId, Item item);

    List<Item> search(String text);

    Optional<Item> get(long id);

    List<Item> getAllItemsFromOwner(long ownerId);
}