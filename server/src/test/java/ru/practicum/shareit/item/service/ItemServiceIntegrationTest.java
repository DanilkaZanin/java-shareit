package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentRequest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemServiceIntegrationTest {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        // Создаем владельца предмета
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        // Создаем пользователя, который будет бронировать предмет
        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        // Создаем предмет
        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwnerId(owner.getId());
        item = itemRepository.save(item);
    }

    @Test
    void shouldSaveItem() {
        ItemRequest request = new ItemRequest();
        request.setName("New Item");
        request.setDescription("New Description");
        request.setAvailable(true);

        ItemDto savedItem = itemService.save(owner.getId(), request);

        assertNotNull(savedItem);
        assertEquals("New Item", savedItem.getName());
        assertEquals("New Description", savedItem.getDescription());
    }

    @Test
    void shouldUpdateItem() {
        ItemUpdateRequest updateRequest = new ItemUpdateRequest();
        updateRequest.setName("Updated Item");
        updateRequest.setDescription("Updated Description");

        ItemDto updatedItem = itemService.update(owner.getId(), item.getId(), updateRequest);

        assertNotNull(updatedItem);
        assertEquals("Updated Item", updatedItem.getName());
        assertEquals("Updated Description", updatedItem.getDescription());
    }

    @Test
    void shouldGetItemById() {
        ItemDto retrievedItem = itemService.get(owner.getId(), item.getId());

        assertNotNull(retrievedItem);
        assertEquals("Test Item", retrievedItem.getName());
    }

    @Test
    void shouldSearchItems() {
        List<ItemDto> items = itemService.search(owner.getId(), "Test");

        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
    }

    @Test
    void shouldSaveComment() {
        // Создаем бронирование для предмета
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);
        bookingRepository.save(booking);

        // Создаем запрос для комментария
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setText("Great item!");

        CommentDto comment = itemService.saveComment(booker.getId(), item.getId(), commentRequest);

        assertNotNull(comment);
        assertEquals("Great item!", comment.getText());
        assertEquals(booker.getName(), comment.getAuthorName());
    }
}
