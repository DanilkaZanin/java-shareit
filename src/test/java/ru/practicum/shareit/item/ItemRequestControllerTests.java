package ru.practicum.shareit.item;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserRequest;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.user.UserEmailConfiguration.email;

@SpringBootTest
class ItemRequestControllerTests {
    private final ItemController itemController;
    private final UserController userController;

    @Autowired
    public ItemRequestControllerTests(ItemController itemController, UserController userController) {
        this.itemController = itemController;
        this.userController = userController;
    }

    private UserRequest getUser() {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail(email());
        userRequest.setName("test");
        return userRequest;
    }

    private ItemRequest getItem() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setName("test");
        itemRequest.setDescription("asdasda");
        itemRequest.setAvailable(true);
        return itemRequest;
    }

    @Test
     void shouldAddItem() {
        UserDto userDto = userController.saveUser(getUser());
        ItemRequest itemRequest = getItem();

        ItemDto itemDto = itemController.saveItem(userDto.getId(), itemRequest);

        assertNotNull(itemDto.getId());
        assertEquals(itemRequest.getName(), itemDto.getName());
        assertEquals(itemRequest.getDescription(), itemDto.getDescription());
        assertEquals(itemRequest.getAvailable(), itemDto.getAvailable());
    }

    @Test
     void shouldPatchItem() {
        UserDto userDto = userController.saveUser(getUser());
        ItemRequest itemRequest = getItem();
        itemController.saveItem(userDto.getId(), itemRequest);

        itemRequest.setDescription("new description");
        ItemDto itemDto = itemController.saveItem(userDto.getId(), itemRequest);

        assertNotNull(itemDto);
        assertEquals("new description", itemDto.getDescription());

    }
}