package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Item {
    private long id;
    @NotNull
    @NotBlank
    @NotEmpty
    private String name;
    @NotNull
    @NotBlank
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private long ownerId;
}
