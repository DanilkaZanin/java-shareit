package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingRequest bookingRequest);

    BookingDto approveOrRejectBooking(Long userId, Long bookingId, Boolean status);

    BookingDto getBooking(Long userId, Long bookingId);

    List<BookingDto> getCustomerBookings(Long userId, State state);

    List<BookingDto> getOwnersBookings(Long userId, State state);
}