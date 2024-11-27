package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingTimeDto;
import ru.practicum.shareit.comment.CommentDto;

import java.util.List;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingTimeDto lastBooking;
    private BookingTimeDto nextBooking;
    private List<CommentDto> comments;
}