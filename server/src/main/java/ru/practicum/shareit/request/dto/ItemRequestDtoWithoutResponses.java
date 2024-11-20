package ru.practicum.shareit.request.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestDtoWithoutResponses {
    Long id;
    String description;
    LocalDateTime created;
}