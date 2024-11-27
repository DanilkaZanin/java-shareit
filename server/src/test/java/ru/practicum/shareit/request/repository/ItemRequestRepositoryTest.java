package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest3;

    @BeforeEach
    void setRequests() {
        user1 = new User();
        user1.setId(null);
        user1.setName("user1");
        user1.setEmail("user1mail@gmail.com");

        user2 = new User();
        user2.setId(null);
        user2.setName("user2");
        user2.setEmail("user2mail@gmail.com");

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        itemRequest1 = new ItemRequest();
        itemRequest1.setId(null);
        itemRequest1.setDescription("Request 1 description");
        itemRequest1.setCreated(LocalDateTime.of(2023, 1, 1, 1, 1));
        itemRequest1.setRequestorId(user1.getId());

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(null);
        itemRequest2.setDescription("Request 2 description");
        itemRequest2.setCreated(LocalDateTime.of(2023, 1, 2, 1, 1));
        itemRequest2.setRequestorId(user2.getId());

        itemRequest3 = new ItemRequest();
        itemRequest3.setId(null);
        itemRequest3.setDescription("Request 3 description");
        itemRequest3.setCreated(LocalDateTime.of(2023, 1, 3, 1, 1));
        itemRequest3.setRequestorId(user1.getId());

        itemRequest1 = itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);
        itemRequest3 = itemRequestRepository.save(itemRequest3);
    }

    @Test
    void shouldFindRequestorRequestsByHisId() {
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(user1.getId());

        assertThat(requests).size().isEqualTo(2);
        assertThat(requests.getFirst()).isEqualTo(itemRequest3);
        assertThat(requests.getLast()).isEqualTo(itemRequest1);
    }

    @Test
    void shouldFindAllRequestsByRequestorId() {
        List<ItemRequest> requests = itemRequestRepository.findAllNotOwnerRequestsSortedByCreatedTimeDesc(user2.getId());

        assertThat(requests).size().isEqualTo(2);
        assertThat(requests.getFirst()).isEqualTo(itemRequest3);
        assertThat(requests.getLast()).isEqualTo(itemRequest1);
    }
}