package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.error.exception.NotAvailableException;
import ru.practicum.shareit.error.exception.NotBookerException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.NotOwnerException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void shouldCreateBookingWhenItemIsAvailable() {
        Long userId = 1L;
        Long itemId = 2L;

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setItemId(itemId);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);

        User user = new User();
        user.setId(userId);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);

        Booking savedBooking = new Booking();
        savedBooking.setId(1L);

        BookingDto bookingDto = new BookingDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.map(bookingRequest, Booking.class)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(savedBooking);
        when(mapper.map(savedBooking, BookingDto.class)).thenReturn(bookingDto);

        BookingDto result = bookingService.create(userId, bookingRequest);

        assertEquals(bookingDto, result);
        verify(itemRepository, times(1)).findById(itemId);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void shouldThrowNotAvailableExceptionWhenItemIsNotAvailable() {
        Long userId = 1L;
        Long itemId = 2L;

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setItemId(itemId);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(false);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        NotAvailableException exception = assertThrows(NotAvailableException.class,
                () -> bookingService.create(userId, bookingRequest));

        assertEquals("Item 2 is not available", exception.getMessage());
        verify(itemRepository, times(1)).findById(itemId);
        verify(userRepository, never()).findById(userId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldApproveBookingWhenUserIsOwnerAndApprovedIsTrue() {
        Long userId = 1L;
        Long bookingId = 3L;

        Booking booking = new Booking();
        Item item = new Item();
        item.setOwnerId(userId);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        Booking updatedBooking = new Booking();
        updatedBooking.setStatus(Status.APPROVED);

        BookingDto bookingDto = new BookingDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(updatedBooking);
        when(mapper.map(updatedBooking, BookingDto.class)).thenReturn(bookingDto);

        BookingDto result = bookingService.approveOrRejectBooking(userId, bookingId, true);

        assertEquals(bookingDto, result);
        assertEquals(Status.APPROVED, booking.getStatus());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void shouldThrowNotOwnerExceptionWhenUserIsNotOwner() {
        Long userId = 1L;
        Long bookingId = 3L;

        Booking booking = new Booking();
        Item item = new Item();
        item.setOwnerId(2L);
        booking.setItem(item);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        NotOwnerException exception = assertThrows(NotOwnerException.class,
                () -> bookingService.approveOrRejectBooking(userId, bookingId, true));

        assertEquals("User 1 is not the owner of the booking", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldThrowNotAvailableExceptionWhenUserHasNotAvailableId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotAvailableException.class,
                () -> bookingService.approveOrRejectBooking(1L, 1L, true));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetBookingWhenBookingIdAndUserIdIsAvailable() {
        Booking booking = new Booking();
        BookingDto bookingDto1 = new BookingDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(mapper.map(booking, BookingDto.class)).thenReturn(bookingDto1);

        BookingDto bookingDto2 = bookingService.getBooking(1L, 1L);

        assertEquals(bookingDto1, bookingDto2);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserIdIsUnavailable() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(1L, 1L));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetAllCustomerBookingsByBookerId() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        BookingDto bookingDto1 = new BookingDto();
        BookingDto bookingDto2 = new BookingDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.getBookingsByBookerId(anyLong())).thenReturn(bookings);
        when(mapper.map(booking1, BookingDto.class)).thenReturn(bookingDto1);
        when(mapper.map(booking2, BookingDto.class)).thenReturn(bookingDto2);

        List<BookingDto> bookingDtos = bookingService.getCustomerBookings(anyLong(), State.ALL);

        assertThat(bookingDtos).hasSize(2);
        assertThat(bookingDtos.get(0)).isEqualTo(bookingDto1);
        assertThat(bookingDtos.get(1)).isEqualTo(bookingDto2);

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingsByBookerId(anyLong());
    }

    @Test
    void shouldGetWaitingCustomerBookingsByBookerId() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        BookingDto bookingDto1 = new BookingDto();
        BookingDto bookingDto2 = new BookingDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.getBookingsByBookerIdAndStatus(anyLong(), eq(Status.WAITING))).thenReturn(bookings);
        when(mapper.map(booking1, BookingDto.class)).thenReturn(bookingDto1);
        when(mapper.map(booking2, BookingDto.class)).thenReturn(bookingDto2);

        List<BookingDto> bookingDtos = bookingService.getCustomerBookings(anyLong(), State.WAITING);

        assertThat(bookingDtos).hasSize(2);
        assertThat(bookingDtos.get(0)).isEqualTo(bookingDto1);
        assertThat(bookingDtos.get(1)).isEqualTo(bookingDto2);

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingsByBookerIdAndStatus(anyLong(), eq(Status.WAITING));
    }

    @Test
    void shouldGetPastCustomerBookingsByBookerId() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        BookingDto bookingDto1 = new BookingDto();
        BookingDto bookingDto2 = new BookingDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.getBookingsByBookerIdAndEndBefore(anyLong(), any(LocalDateTime.class))).thenReturn(bookings);
        when(mapper.map(booking1, BookingDto.class)).thenReturn(bookingDto1);
        when(mapper.map(booking2, BookingDto.class)).thenReturn(bookingDto2);

        List<BookingDto> bookingDtos = bookingService.getCustomerBookings(anyLong(), State.PAST);

        assertThat(bookingDtos).hasSize(2);
        assertThat(bookingDtos.get(0)).isEqualTo(bookingDto1);
        assertThat(bookingDtos.get(1)).isEqualTo(bookingDto2);

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingsByBookerIdAndEndBefore(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void shouldGetCurrentCustomerBookingsByBookerId() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        BookingDto bookingDto1 = new BookingDto();
        BookingDto bookingDto2 = new BookingDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.getBookingsByBookerIdAndCurrentTime(anyLong(), any(LocalDateTime.class))).thenReturn(bookings);
        when(mapper.map(booking1, BookingDto.class)).thenReturn(bookingDto1);
        when(mapper.map(booking2, BookingDto.class)).thenReturn(bookingDto2);

        List<BookingDto> bookingDtos = bookingService.getCustomerBookings(anyLong(), State.CURRENT);

        assertThat(bookingDtos).hasSize(2);
        assertThat(bookingDtos.get(0)).isEqualTo(bookingDto1);
        assertThat(bookingDtos.get(1)).isEqualTo(bookingDto2);

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingsByBookerIdAndCurrentTime(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void shouldGetFutureCustomerBookingsByBookerId() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        BookingDto bookingDto1 = new BookingDto();
        BookingDto bookingDto2 = new BookingDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.getBookingsByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class))).thenReturn(bookings);
        when(mapper.map(booking1, BookingDto.class)).thenReturn(bookingDto1);
        when(mapper.map(booking2, BookingDto.class)).thenReturn(bookingDto2);

        List<BookingDto> bookingDtos = bookingService.getCustomerBookings(anyLong(), State.FUTURE);

        assertThat(bookingDtos).hasSize(2);
        assertThat(bookingDtos.get(0)).isEqualTo(bookingDto1);
        assertThat(bookingDtos.get(1)).isEqualTo(bookingDto2);

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingsByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void shouldGetRejectedCustomerBookingsByBookerId() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        BookingDto bookingDto1 = new BookingDto();
        BookingDto bookingDto2 = new BookingDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.getBookingsByBookerIdAndStatus(anyLong(), eq(Status.REJECTED))).thenReturn(bookings);
        when(mapper.map(booking1, BookingDto.class)).thenReturn(bookingDto1);
        when(mapper.map(booking2, BookingDto.class)).thenReturn(bookingDto2);

        List<BookingDto> bookingDtos = bookingService.getCustomerBookings(anyLong(), State.REJECTED);

        assertThat(bookingDtos).hasSize(2);
        assertThat(bookingDtos.get(0)).isEqualTo(bookingDto1);
        assertThat(bookingDtos.get(1)).isEqualTo(bookingDto2);

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingsByBookerIdAndStatus(anyLong(), eq(Status.REJECTED));
    }

    @Test
    void shouldThrowNotBookerExceptionWhenBookerIdIsUnavailable() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotBookerException.class, () -> bookingService.getCustomerBookings(1L, State.ALL));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetAllOwnerBookingsByBookerId() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        BookingDto bookingDto1 = new BookingDto();
        BookingDto bookingDto2 = new BookingDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.getBookingsByOwnerId(anyLong())).thenReturn(bookings);
        when(mapper.map(booking1, BookingDto.class)).thenReturn(bookingDto1);
        when(mapper.map(booking2, BookingDto.class)).thenReturn(bookingDto2);

        List<BookingDto> bookingDtos = bookingService.getOwnersBookings(anyLong(), State.ALL);

        assertThat(bookingDtos).hasSize(2);
        assertThat(bookingDtos.get(0)).isEqualTo(bookingDto1);
        assertThat(bookingDtos.get(1)).isEqualTo(bookingDto2);

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingsByOwnerId(anyLong());
    }

    @Test
    void shouldGetWaitingOwnerBookingsByOwnerId() {
        Booking booking1 = new Booking();
        BookingDto bookingDto1 = new BookingDto();
        List<Booking> bookings = List.of(booking1);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.getBookingsByOwnerIdAndStatus(anyLong(), eq(Status.WAITING))).thenReturn(bookings);
        when(mapper.map(booking1, BookingDto.class)).thenReturn(bookingDto1);

        List<BookingDto> bookingDtos = bookingService.getOwnersBookings(anyLong(), State.WAITING);

        assertThat(bookingDtos).hasSize(1);
        assertThat(bookingDtos.getFirst()).isEqualTo(bookingDto1);

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingsByOwnerIdAndStatus(anyLong(), eq(Status.WAITING));
    }

    @Test
    void shouldGetPastOwnerBookingsByOwnerId() {
        Booking booking1 = new Booking();
        BookingDto bookingDto1 = new BookingDto();
        List<Booking> bookings = List.of(booking1);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.getBookingsByOwnerIdAndEndBefore(anyLong(), any(LocalDateTime.class)))
                .thenReturn(bookings);
        when(mapper.map(booking1, BookingDto.class)).thenReturn(bookingDto1);

        List<BookingDto> bookingDtos = bookingService.getOwnersBookings(anyLong(), State.PAST);

        assertThat(bookingDtos).hasSize(1);
        assertThat(bookingDtos.getFirst()).isEqualTo(bookingDto1);

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingsByOwnerIdAndEndBefore(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void shouldGetCurrentOwnerBookingsByOwnerId() {
        Booking booking1 = new Booking();
        BookingDto bookingDto1 = new BookingDto();
        List<Booking> bookings = List.of(booking1);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.getBookingsByOwnerIdAndCurrentTime(anyLong(), any(LocalDateTime.class)))
                .thenReturn(bookings);
        when(mapper.map(booking1, BookingDto.class)).thenReturn(bookingDto1);

        List<BookingDto> bookingDtos = bookingService.getOwnersBookings(anyLong(), State.CURRENT);

        assertThat(bookingDtos).hasSize(1);
        assertThat(bookingDtos.getFirst()).isEqualTo(bookingDto1);

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingsByOwnerIdAndCurrentTime(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void shouldGetFutureOwnerBookingsByOwnerId() {
        Booking booking1 = new Booking();
        BookingDto bookingDto1 = new BookingDto();
        List<Booking> bookings = List.of(booking1);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.getBookingsByOwnerIdAndStartAfter(anyLong(), any(LocalDateTime.class)))
                .thenReturn(bookings);
        when(mapper.map(booking1, BookingDto.class)).thenReturn(bookingDto1);

        List<BookingDto> bookingDtos = bookingService.getOwnersBookings(anyLong(), State.FUTURE);

        assertThat(bookingDtos).hasSize(1);
        assertThat(bookingDtos.getFirst()).isEqualTo(bookingDto1);

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingsByOwnerIdAndStartAfter(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void shouldGetRejectedOwnerBookingsByOwnerId() {
        Booking booking1 = new Booking();
        BookingDto bookingDto1 = new BookingDto();
        List<Booking> bookings = List.of(booking1);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.getBookingsByOwnerIdAndStatus(anyLong(), eq(Status.REJECTED))).thenReturn(bookings);
        when(mapper.map(booking1, BookingDto.class)).thenReturn(bookingDto1);

        List<BookingDto> bookingDtos = bookingService.getOwnersBookings(anyLong(), State.REJECTED);

        assertThat(bookingDtos).hasSize(1);
        assertThat(bookingDtos.getFirst()).isEqualTo(bookingDto1);

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingsByOwnerIdAndStatus(anyLong(), eq(Status.REJECTED));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenOwnerIdIsUnavailable() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getOwnersBookings(1L, State.ALL));

        verify(userRepository, times(1)).findById(anyLong());
    }
}