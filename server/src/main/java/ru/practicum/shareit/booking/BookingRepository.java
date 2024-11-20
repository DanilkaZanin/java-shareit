package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> getBookingsByBookerId(Long bookerId);

    List<Booking> getBookingsByBookerIdAndStatus(Long bookerId, Status status);

    List<Booking> getBookingsByBookerIdAndEndBefore(Long bookerId, LocalDateTime endBefore);

    List<Booking> getBookingsByBookerIdAndStartAfter(Long bookerId, LocalDateTime startAfter);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND :currentTime BETWEEN b.start and b.end")
    List<Booking> getBookingsByBookerIdAndCurrentTime(Long bookerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN b.item i WHERE i.ownerId = :ownerId")
    List<Booking> getBookingsByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b JOIN b.item i WHERE i.ownerId = :ownerId AND b.status = :status")
    List<Booking> getBookingsByOwnerIdAndStatus(Long ownerId, Status status);

    @Query("SELECT b FROM Booking b JOIN b.item i WHERE i.ownerId = :ownerId AND b.end < :endBefore")
    List<Booking> getBookingsByOwnerIdAndEndBefore(Long ownerId, LocalDateTime endBefore);

    @Query("SELECT b FROM Booking b JOIN b.item i WHERE i.ownerId = :ownerId AND b.start > :startAfter")
    List<Booking> getBookingsByOwnerIdAndStartAfter(Long ownerId, LocalDateTime startAfter);

    @Query("SELECT b FROM Booking b JOIN b.item i WHERE i.ownerId = :ownerId AND :currentTime BETWEEN b.start and b.end")
    List<Booking> getBookingsByOwnerIdAndCurrentTime(Long ownerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.item i " +
            "JOIN b.booker bo " +
            "WHERE i.id = :itemId AND bo.id = :bookerId AND b.end < :endBefore")
    Optional<Booking> getBookingByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime endBefore);

    List<Booking> getBookingsByItemId(Long itemId);
}