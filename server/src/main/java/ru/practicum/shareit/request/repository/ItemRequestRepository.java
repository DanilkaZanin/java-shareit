package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long id);

    @Query("SELECT r FROM ItemRequest r WHERE r.id != :userId ORDER BY r.created DESC")
    List<ItemRequest> findAllNotOwnerRequestsSortedByCreatedTimeDesc(Long userId);
}