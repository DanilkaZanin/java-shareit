package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithoutResponses;
import ru.practicum.shareit.request.dto.ItemRequestRequset;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemRequestServiceIntegrationTest {

    private final ItemRequestService itemRequestService;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User user;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        // Создаем пользователя
        user = new User();
        user.setName("Requester");
        user.setEmail("requester@example.com");
        user = userRepository.save(user);

        // Создаем другого пользователя
        anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");
        anotherUser = userRepository.save(anotherUser);
    }

    @Test
    void shouldAddRequest() {
        ItemRequestRequset request = new ItemRequestRequset();
        request.setDescription("Need a laptop");

        ItemRequestDtoWithoutResponses addedRequest = itemRequestService.addRequest(request, user.getId());

        assertNotNull(addedRequest);
        assertEquals("Need a laptop", addedRequest.getDescription());
        assertNotNull(addedRequest.getCreated());
        assertNotNull(addedRequest.getId());
    }

    @Test
    void shouldGetRequestById() {
        // Создаем запрос
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Need a monitor");
        itemRequest.setRequestorId(user.getId());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);

        // Добавляем предмет к запросу
        Item item = new Item();
        item.setName("Monitor");
        item.setDescription("HD Monitor");
        item.setAvailable(true);
        item.setOwnerId(anotherUser.getId());
        item.setRequestId(itemRequest.getId());
        itemRepository.save(item);

        ItemRequestDto requestDto = itemRequestService.getRequest(itemRequest.getId(), user.getId());

        assertNotNull(requestDto);
        assertEquals("Need a monitor", requestDto.getDescription());
        assertEquals(1, requestDto.getItems().size());
        assertEquals("Monitor", requestDto.getItems().getFirst().getName());
    }

    @Test
    void shouldGetMyRequests() {
        // Создаем запросы для пользователя
        ItemRequest request1 = new ItemRequest();
        request1.setDescription("Need a keyboard");
        request1.setRequestorId(user.getId());
        request1.setCreated(LocalDateTime.now().minusDays(1));
        itemRequestRepository.save(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Need a mouse");
        request2.setRequestorId(user.getId());
        request2.setCreated(LocalDateTime.now());
        itemRequestRepository.save(request2);

        List<ItemRequestDto> requests = itemRequestService.getMyRequests(user.getId());

        assertNotNull(requests);
        assertEquals(2, requests.size());
        assertEquals("Need a mouse", requests.get(0).getDescription());
        assertEquals("Need a keyboard", requests.get(1).getDescription());
    }
}