package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true " +
            "AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))"
    )
    List<Item> searchItemsByText(String text);

    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.comments c WHERE i.id = :id")
    Optional<Item> findItemByIdWithComments(Long id);

    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.comments c WHERE i.ownerId = :ownerId")
    List<Item> findItemsByOwnerIdWithComments(Long ownerId);
    List<ItemResponseDto> findItemsByRequestId(Long requestId);
}