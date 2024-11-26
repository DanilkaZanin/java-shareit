package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "/booking/service/test-data.sql")
class BookingServiceIntegrationTest {

    private final BookingService bookingService;

    @Test
    void shouldApproveBooking() {
        Long ownerId = 1L;
        Long bookingId = 1L;

        BookingDto updatedBooking = bookingService.approveOrRejectBooking(ownerId, bookingId, true);

        assertThat(updatedBooking.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void shouldRejectBooking() {
        Long ownerId = 1L;
        Long bookingId = 1L;

        BookingDto updatedBooking = bookingService.approveOrRejectBooking(ownerId, bookingId, false);

        assertThat(updatedBooking.getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    void shouldGetBookingById() {
        Long userId = 2L;
        Long bookingId = 1L;

        BookingDto booking = bookingService.getBooking(userId, bookingId);

        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isEqualTo(bookingId);
        assertThat(booking.getBooker().getId()).isEqualTo(2L);
    }

    @Test
    void shouldGetCustomerBookings() {
        Long userId = 2L;

        List<BookingDto> bookings = bookingService.getCustomerBookings(userId, State.ALL);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getBooker().getId()).isEqualTo(2L);
    }

    @Test
    void shouldGetOwnerBookings() {
        Long ownerId = 1L;

        List<BookingDto> bookings = bookingService.getOwnersBookings(ownerId, State.ALL);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.getFirst().getItem().getOwnerId()).isEqualTo(1L);
    }
}