package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemRepository;
    private final UserService userService;

    public ItemDto save(long ownerId, Item item) {
        userService.get(ownerId);
        item.setOwnerId(ownerId);
        return itemRepository.create(item);
    }

    public ItemDto update(long ownerId, long itemId, ItemUpdateRequest item) {
        userService.get(ownerId);
        if (itemRepository.get(itemId).isPresent()) {
            return itemRepository.update(ownerId, itemId, item);
        }
        return null;
    }

    public List<ItemDto> search(long ownerId, String text) {
        //userService.get(ownerId);
        return itemRepository.search(text);
    }

    public ItemDto get(long ownerId, long itemId) {
        userService.get(ownerId);
        if (itemRepository.get(itemId).isPresent()) {
            return itemRepository.get(itemId).get();
        }
        return null;
    }

    public List<ItemDto> getAllItemsFromOwner(long ownerId) {
        userService.get(ownerId);
        return itemRepository.getAllItemsFromOwner(ownerId);
    }
}