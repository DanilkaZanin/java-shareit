package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.exception.NotAvailableException;
import ru.practicum.shareit.error.exception.NotBookerException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.NotOwnerException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ModelMapper mapper;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(Long userId, BookingRequest bookingRequest) {
        Booking booking = mapper.map(bookingRequest, Booking.class);

        Item item = itemRepository.findById(bookingRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new NotAvailableException(String.format("Item %s is not available", bookingRequest.getItemId()));
        }

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Booker not found"));

        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return mapper.map(savedBooking, BookingDto.class);
    }

    @Override
    public BookingDto approveOrRejectBooking(Long userId, Long bookingId, Boolean approved) {
        if (!checkUserBuId(userId)) {
            throw new NotAvailableException(String.format("Unavailable owner id %d", userId));
        }
        Booking booking = getBooking(bookingId);

        if (!Objects.equals(booking.getItem().getOwnerId(), userId)) {
            throw new NotOwnerException(String.format("User %d is not the owner of the booking", userId));
        }

        setBookingStatus(booking, approved);
        return mapper.map(bookingRepository.save(booking), BookingDto.class);
    }

    private void setBookingStatus(Booking booking, boolean status) {
        if (status) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBooking(Long userId, Long bookingId) {
        if (!checkUserBuId(userId)) {
            throw new NotFoundException(String.format("Unavailable user id %d", userId));
        }

        Booking booking = getBooking(bookingId);

        if (!Objects.equals(booking.getBooker().getId(), userId)) {
            throw new NotBookerException(String.format("User %d is not the owner of the booking", userId));
        }

        return mapper.map(booking, BookingDto.class);
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking %d not found", bookingId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getCustomerBookings(Long userId, State state) {
        if (!checkUserBuId(userId)) {
            throw new NotFoundException(String.format("Unavailable customer id %d", userId));
        }

        List<Booking> bookings;
        switch (state) {
            case ALL -> bookings = bookingRepository.getBookingsByBookerId(userId);
            case WAITING -> bookings = bookingRepository.getBookingsByBookerIdAndStatus(userId, Status.WAITING);
            case PAST -> bookings = bookingRepository.getBookingsByBookerIdAndEndBefore(userId, LocalDateTime.now());
            case CURRENT ->
                    bookings = bookingRepository.getBookingsByBookerIdAndCurrentTime(userId, LocalDateTime.now());
            case FUTURE -> bookings = bookingRepository.getBookingsByBookerIdAndStartAfter(userId, LocalDateTime.now());
            case REJECTED -> bookings = bookingRepository.getBookingsByBookerIdAndStatus(userId, Status.REJECTED);
            default -> throw new NotAvailableException(String.format("State %s is not available", state));
        }
        return bookings.stream().map(booking -> mapper.map(booking, BookingDto.class)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getOwnersBookings(Long userId, State state) {
        if (!checkUserBuId(userId)) {
            throw new NotFoundException(String.format("Unavailable owner id %d", userId));
        }

        List<Booking> bookings;
        switch (state) {
            case ALL -> bookings = bookingRepository.getBookingsByOwnerId(userId);
            case WAITING -> bookings = bookingRepository.getBookingsByOwnerIdAndStatus(userId, Status.WAITING);
            case PAST -> bookings = bookingRepository.getBookingsByOwnerIdAndEndBefore(userId, LocalDateTime.now());
            case CURRENT ->
                    bookings = bookingRepository.getBookingsByOwnerIdAndCurrentTime(userId, LocalDateTime.now());
            case FUTURE -> bookings = bookingRepository.getBookingsByOwnerIdAndStartAfter(userId, LocalDateTime.now());
            case REJECTED -> bookings = bookingRepository.getBookingsByOwnerIdAndStatus(userId, Status.REJECTED);
            default -> throw new NotAvailableException(String.format("State %s is not available", state));
        }
        return bookings.stream().map(booking -> mapper.map(booking, BookingDto.class)).toList();
    }

    private boolean checkUserBuId(Long userId) {
        return userRepository.findById(userId).isPresent();
    }
}