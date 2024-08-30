package ru.practicum.shareit;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.UserEmailConfiguration.email;

@SpringBootTest
public class ItemControllerTests {
    @Autowired
    private ItemController itemController;
    @Autowired
    private UserController userController;


    private User getUser() {
        User user = new User();
        user.setEmail(email());
        user.setName("test");
        return user;
    }

    private Item getItem() {
        Item item = new Item();
        item.setName("test");
        item.setDescription("asdasda");
        item.setAvailable(true);
        return item;
    }

    @Test
    public void shouldAddItem() {
        UserDto userDto = userController.saveUser(getUser());
        Item item = getItem();

        ItemDto itemDto = itemController.saveItem(userDto.getId(), item);

        assertNotNull(itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    public void shouldPatchItem() {
        UserDto userDto = userController.saveUser(getUser());
        Item item = getItem();
        itemController.saveItem(userDto.getId(), item);

        item.setDescription("new description");
        ItemDto itemDto = itemController.saveItem(userDto.getId(), item);

        assertNotNull(itemDto);
        assertEquals("new description", itemDto.getDescription());

    }

    @Test
    public void shouldGetItem() {
        UserDto userDto = userController.saveUser(getUser());
        Item item = getItem();
        ItemDto itemDto = itemController.saveItem(userDto.getId(), item);

        ItemDto getItem = itemController.getItem(userDto.getId(), item.getId());

        assertEquals(itemDto, getItem);
    }

    @Test
    public void shouldGetItemsByUserId() {
        UserDto userDto = userController.saveUser(getUser());
        Item item1 = getItem();
        Item item2 = getItem();
        ItemDto itemDto1 = itemController.saveItem(userDto.getId(), item1);
        ItemDto itemDto2 = itemController.saveItem(userDto.getId(), item2);

        List<ItemDto> itemDtos = itemController.getItems(userDto.getId());

        assertEquals(2, itemDtos.size());
        assertTrue(itemDtos.contains(itemDto1));
        assertTrue(itemDtos.contains(itemDto2));
    }

    @Test
    public void shouldSearchItem() {
        UserDto userDto = userController.saveUser(getUser());
        Item item = getItem();
        ItemDto itemDto = itemController.saveItem(userDto.getId(), item);

        List<ItemDto> getItem = itemController.searchItem(userDto.getId(), itemDto.getName());

        assertEquals(1, getItem.size());
        assertEquals(itemDto, getItem.getFirst());
    }
}