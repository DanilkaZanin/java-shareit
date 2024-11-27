package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = "/item/repository/test-data.sql")
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void shouldSearchItemsByText() {
        List<Item> items = itemRepository.searchItemsByText("item");

        assertThat(items).hasSize(1);
        assertThat(items.getFirst().getName()).isEqualTo("Item 1");
    }

    @Test
    void shouldFindItemByIdWithComments() {
        Optional<Item> item = itemRepository.findItemByIdWithComments(1L);

        assertThat(item).isPresent();
        assertThat(item.get().getName()).isEqualTo("Item 1");
        assertThat(item.get().getComments()).hasSize(1);
        assertThat(item.get().getComments().getFirst().getText()).isEqualTo("Great item!");
    }

    @Test
    void shouldFindItemsByOwnerIdWithComments() {
        List<Item> items = itemRepository.findItemsByOwnerIdWithComments(2L);

        assertThat(items).hasSize(1);
        assertThat(items.getFirst().getName()).isEqualTo("Item 1");
        assertThat(items.getFirst().getComments()).hasSize(1);
    }

    @Test
    void shouldFindItemsByRequestId() {
        List<ItemResponseDto> items = itemRepository.findItemsByRequestId(1L);

        assertThat(items).hasSize(1);
        assertThat(items.getFirst().getName()).isEqualTo("Item 1");
        assertThat(items.getFirst().getId()).isEqualTo(1);
    }
}