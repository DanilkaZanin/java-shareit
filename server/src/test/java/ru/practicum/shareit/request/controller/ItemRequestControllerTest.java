package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithoutResponses;
import ru.practicum.shareit.request.dto.ItemRequestRequset;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequestRequset itemRequestRequset;
    private ItemRequestDtoWithoutResponses requestDtoWithoutResponses;
    private ItemRequestDto requestDto;

    @BeforeEach
    void setUp() {
        itemRequestRequset = new ItemRequestRequset();
        itemRequestRequset.setDescription("Test description");

        requestDtoWithoutResponses = new ItemRequestDtoWithoutResponses();
        requestDtoWithoutResponses.setId(1L);
        requestDtoWithoutResponses.setDescription("Test description");
        requestDtoWithoutResponses.setCreated(LocalDateTime.now());

        requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Test description");
        requestDto.setCreated(LocalDateTime.now());
        requestDto.setRequestorId(1L);
        requestDto.setItems(List.of());
    }

    @Test
    void addRequestShouldReturnRequestDtoWithoutResponses() throws Exception {
        when(itemRequestService.addRequest(any(ItemRequestRequset.class), anyLong()))
                .thenReturn(requestDtoWithoutResponses);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestRequset)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Test description"));

        verify(itemRequestService, times(1)).addRequest(any(ItemRequestRequset.class), anyLong());
    }

    @Test
    void getMyRequestsShouldReturnListOfItemRequestDtos() throws Exception {
        List<ItemRequestDto> requests = Arrays.asList(requestDto, requestDto);
        when(itemRequestService.getMyRequests(anyLong())).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Test description"));

        verify(itemRequestService, times(1)).getMyRequests(anyLong());
    }

    @Test
    void getRequestsAllShouldReturnListOfItemRequestDtosWithoutResponses() throws Exception {
        List<ItemRequestDtoWithoutResponses> requests = Arrays.asList(requestDtoWithoutResponses, requestDtoWithoutResponses);
        when(itemRequestService.getAllRequests(anyLong())).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Test description"));

        verify(itemRequestService, times(1)).getAllRequests(anyLong());
    }

    @Test
    void getRequestShouldReturnRequestDto() throws Exception {
        when(itemRequestService.getRequest(anyLong(), anyLong())).thenReturn(requestDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.requestorId").value(1L));

        verify(itemRequestService, times(1)).getRequest(anyLong(), anyLong());
    }
}