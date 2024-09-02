package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepository implements ItemStorage {
    private final Map<Long, Item> items;
    private long customIdCounter = 0;

    private long nextId() {
        return ++customIdCounter;
    }

    @Override
    public Item create(Item item) {
        item.setId(nextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(long ownerId, long itemId, Item item) {
        if (items.get(itemId).getOwnerId() != ownerId) {
            throw new NotFoundException("Item with id " + itemId + " does not own owner with id " + ownerId);
        }
        items.put(itemId, item);
        return item;
    }

    @Override
    public List<Item> search(String text) {
        if (items == null || text == null || text.isEmpty() || items.isEmpty()) {
            return Collections.emptyList();
        }
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toUpperCase().contains(text.toUpperCase())
                        || item.getDescription().toUpperCase().contains(text.toUpperCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> get(long id) {
        if (items.containsKey(id)) {
            return Optional.of(items.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<Item> getAllItemsFromOwner(long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId() == ownerId)
                .toList();
    }
}