package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = "/booking/repository/test-data.sql")
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void shouldReturnBookingsByBookerId() {
        List<Booking> bookings = bookingRepository.getBookingsByBookerId(1L);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getBooker().getId()).isEqualTo(1L);
        assertThat(bookings.getFirst().getItem().getId()).isEqualTo(1L);
    }

    @Test
    void shouldReturnBookingsByBookerIdAndStatus() {
        List<Booking> bookings = bookingRepository.getBookingsByBookerIdAndStatus(1L, Status.APPROVED);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void shouldReturnBookingsByBookerIdAndEndBefore() {
        List<Booking> bookings = bookingRepository.getBookingsByBookerIdAndEndBefore(1L, LocalDateTime.now().plusDays(1));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getEnd()).isBefore(LocalDateTime.now().plusDays(1));
    }

    @Test
    void shouldReturnBookingsByBookerIdAndStartAfter() {
        List<Booking> bookings = bookingRepository.getBookingsByBookerIdAndStartAfter(1L, LocalDateTime.now());

        assertThat(bookings).isEmpty();
    }

    @Test
    void shouldReturnBookingsByBookerIdAndCurrentTime() {
        List<Booking> bookings = bookingRepository.getBookingsByBookerIdAndCurrentTime(1L, LocalDateTime.now());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getBooker().getId()).isEqualTo(1L);
    }

    @Test
    void shouldReturnBookingsByOwnerId() {
        List<Booking> bookings = bookingRepository.getBookingsByOwnerId(2L);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getItem().getOwnerId()).isEqualTo(2L);
    }

    @Test
    void shouldReturnBookingsByOwnerIdAndStatus() {
        List<Booking> bookings = bookingRepository.getBookingsByOwnerIdAndStatus(2L, Status.APPROVED);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void shouldReturnBookingsByOwnerIdAndEndBefore() {
        List<Booking> bookings = bookingRepository.getBookingsByOwnerIdAndEndBefore(2L, LocalDateTime.now().plusDays(1));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getEnd()).isBefore(LocalDateTime.now().plusDays(1));
    }

    @Test
    void shouldReturnBookingsByOwnerIdAndCurrentTime() {
        List<Booking> bookings = bookingRepository.getBookingsByOwnerIdAndCurrentTime(2L, LocalDateTime.now());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getItem().getOwnerId()).isEqualTo(2L);
    }

    @Test
    void shouldReturnBookingByItemIdAndBookerIdAndEndBefore() {
        Optional<Booking> booking = bookingRepository.getBookingByItemIdAndBookerIdAndEndBefore(1L, 1L, LocalDateTime.now().plusDays(1));

        assertThat(booking).isPresent();
        assertThat(booking.get().getItem().getId()).isEqualTo(1L);
        assertThat(booking.get().getBooker().getId()).isEqualTo(1L);
        assertThat(booking.get().getEnd()).isBefore(LocalDateTime.now().plusDays(1));
    }

    @Test
    void shouldReturnBookingsByItemId() {
        List<Booking> bookings = bookingRepository.getBookingsByItemId(1L);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getItem().getId()).isEqualTo(1L);
    }
}