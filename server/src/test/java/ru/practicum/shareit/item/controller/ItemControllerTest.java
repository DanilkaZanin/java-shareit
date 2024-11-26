package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentRequest;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto itemDto;
    private CommentDto commentDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        itemDto.setComments(List.of());

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Great item!");
        commentDto.setAuthorName("Test User");
        commentDto.setCreated(LocalDateTime.now());

        itemRequest = new ItemRequest();
        itemRequest.setName("New Item");
        itemRequest.setDescription("New Description");
        itemRequest.setAvailable(true);
    }

    @Test
    void saveItemShouldReturnItemDto() throws Exception {
        when(itemService.save(anyLong(), any(ItemRequest.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService, times(1)).save(anyLong(), any(ItemRequest.class));
    }

    @Test
    void updateItemShouldReturnUpdatedItemDto() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"));

        verify(itemService, times(1)).update(anyLong(), anyLong(), any());
    }

    @Test
    void getItemShouldReturnItemDto() throws Exception {
        when(itemService.get(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"));

        verify(itemService, times(1)).get(anyLong(), anyLong());
    }

    @Test
    void getItemsShouldReturnListOfItemDtos() throws Exception {
        List<ItemDto> items = Arrays.asList(itemDto, itemDto);
        when(itemService.getAllItemsFromOwner(anyLong())).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(itemService, times(1)).getAllItemsFromOwner(anyLong());
    }

    @Test
    void searchItemShouldReturnListOfItemDtos() throws Exception {
        List<ItemDto> items = Arrays.asList(itemDto, itemDto);
        when(itemService.search(anyLong(), anyString())).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(itemService, times(1)).search(anyLong(), anyString());
    }

    @Test
    void saveCommentShouldReturnCommentDto() throws Exception {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setText("Great item!");

        when(itemService.saveComment(anyLong(), anyLong(), any(CommentRequest.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").value("Test User"));

        verify(itemService, times(1)).saveComment(anyLong(), anyLong(), any(CommentRequest.class));
    }
}
