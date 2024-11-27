package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDto bookingDto;
    private BookingRequest bookingRequest;

    @BeforeEach
    void setUp() {
        // Инициализация объектов для тестов
        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2024, 12, 1, 10, 0)); // LocalDateTime
        bookingDto.setEnd(LocalDateTime.of(2024, 12, 1, 12, 0)); // LocalDateTime
        bookingDto.setStatus(Status.WAITING); // Используем Status enum

        bookingRequest = new BookingRequest();
        bookingRequest.setStart(LocalDateTime.of(2024, 12, 1, 10, 0)); // LocalDateTime
        bookingRequest.setEnd(LocalDateTime.of(2024, 12, 1, 12, 0)); // LocalDateTime
    }

    @Test
    void createBookingShouldReturnBookingDto() throws Exception {
        when(bookingService.create(anyLong(), any(BookingRequest.class))).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.start").value("2024-12-01T10:00:00"))
                .andExpect(jsonPath("$.end").value("2024-12-01T12:00:00"))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService, times(1)).create(anyLong(), any(BookingRequest.class));
    }

    @Test
    void approveOrRejectBookingShouldReturnBookingDto() throws Exception {
        when(bookingService.approveOrRejectBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.start").value("2024-12-01T10:00:00"))
                .andExpect(jsonPath("$.end").value("2024-12-01T12:00:00"))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService, times(1)).approveOrRejectBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getBookingShouldReturnBookingDto() throws Exception {
        // Мокаем сервис для получения бронирования
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.start").value("2024-12-01T10:00:00"))
                .andExpect(jsonPath("$.end").value("2024-12-01T12:00:00"))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService, times(1)).getBooking(anyLong(), anyLong());
    }

    @Test
    void getCustomerBookingsShouldReturnListOfBookings() throws Exception {
        // Мокаем сервис для получения всех бронирований клиента
        List<BookingDto> bookingList = Arrays.asList(bookingDto, bookingDto);
        when(bookingService.getCustomerBookings(anyLong(), eq(State.ALL))).thenReturn(bookingList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(1L));

        verify(bookingService, times(1)).getCustomerBookings(anyLong(), eq(State.ALL));
    }

    @Test
    void getOwnersBookingsShouldReturnListOfBookings() throws Exception {
        // Мокаем сервис для получения всех бронирований владельца
        List<BookingDto> bookingList = Arrays.asList(bookingDto, bookingDto);
        when(bookingService.getOwnersBookings(anyLong(), eq(State.ALL))).thenReturn(bookingList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(1L));

        verify(bookingService, times(1)).getOwnersBookings(anyLong(), eq(State.ALL));
    }
}