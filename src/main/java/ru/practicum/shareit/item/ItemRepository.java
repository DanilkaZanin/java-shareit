package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.NotNullCondition;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ItemRepository implements ItemStorage {
    private Map<Long, Item> items;
    private ModelMapper modelMapper;
    private long customIdCounter;

    private long nextId() {
        return ++customIdCounter;
    }

    @Override
    public ItemDto create(Item item) {
        item.setId(nextId());
        items.put(item.getId(), item);
        return modelMapper.map(item, ItemDto.class);
    }

    @Override
    public ItemDto update(long ownerId, long itemId, ItemUpdateRequest item) {
        if (items.get(itemId).getOwnerId() != ownerId) {
            throw new NotFoundException("Item with id " + itemId + " does not own owner with id " + ownerId);
        }

        Item itemToUpdate = items.get(itemId);
        modelMapper.getConfiguration().setPropertyCondition(new NotNullCondition());
        modelMapper.map(item, itemToUpdate);

        items.put(itemId, itemToUpdate);
        return modelMapper.map(item, ItemDto.class);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (items == null || text == null || text.isEmpty() || items.isEmpty()) {
            return Collections.emptyList();
        }
        List<ItemDto> list = items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toUpperCase().contains(text.toUpperCase()))
                .map(item -> modelMapper.map(item, ItemDto.class))
                .collect(Collectors.toList());

        return list;
    }

    @Override
    public Optional<ItemDto> get(long id) {
        if (items.containsKey(id)) {
            return Optional.of(modelMapper.map(items.get(id), ItemDto.class));
        }
        return Optional.empty();
    }

    @Override
    public List<ItemDto> getAllItemsFromOwner(long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId() == ownerId)
                .map(item -> modelMapper.map(item, ItemDto.class)).toList();
    }
}