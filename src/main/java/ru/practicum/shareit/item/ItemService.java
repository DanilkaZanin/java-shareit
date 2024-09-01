package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    public ItemDto save(long ownerId, ru.practicum.shareit.item.dto.ItemRequest itemRequest) {
        userService.get(ownerId);
        Item item = modelMapper.map(itemRequest, Item.class);
        item.setOwnerId(ownerId);
        return modelMapper.map(itemRepository.create(item), ItemDto.class);
    }

    public ItemDto update(long ownerId, long itemId, ItemUpdateRequest item) {
        userService.get(ownerId);
        if (itemRepository.get(itemId).isPresent()) {
            Item itemToUpdate = itemRepository.get(itemId).get();
            modelMapper.map(item, itemToUpdate);
            return modelMapper.map(itemRepository.update(ownerId,itemId, itemToUpdate), ItemDto.class);
        }
        return null;
    }

    public List<ItemDto> search(long ownerId, String text) {
        //userService.get(ownerId);
        return itemRepository.search(text).stream().map(item -> modelMapper.map(item, ItemDto.class)).toList();
    }

    public ItemDto get(long ownerId, long itemId) {
        userService.get(ownerId);
        if (itemRepository.get(itemId).isPresent()) {
            return modelMapper.map(itemRepository.get(itemId).get(), ItemDto.class);
        }
        return null;
    }

    public List<ItemDto> getAllItemsFromOwner(long ownerId) {
        userService.get(ownerId);
        return itemRepository.getAllItemsFromOwner(ownerId).stream()
                .map(item -> modelMapper.map(item, ItemDto.class))
                .toList();
    }
}