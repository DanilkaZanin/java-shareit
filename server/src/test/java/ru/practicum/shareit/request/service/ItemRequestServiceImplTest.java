package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithoutResponses;
import ru.practicum.shareit.request.dto.ItemRequestRequset;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.impl.ItemRequestServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemService itemService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void addRequestShouldAddRequest() {
        Long userId = 1L;
        ItemRequestRequset itemRequestRequset = new ItemRequestRequset();
        ItemRequest itemRequest = new ItemRequest();
        when(modelMapper.map(itemRequestRequset, ItemRequest.class)).thenReturn(itemRequest);
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);

        itemRequestService.addRequest(itemRequestRequset, userId);

        assertThat(itemRequest.getCreated()).isNotNull();
        assertThat(itemRequest.getRequestorId()).isEqualTo(userId);
        verify(modelMapper, times(1)).map(itemRequestRequset, ItemRequest.class);
        verify(itemRequestRepository, times(1)).save(itemRequest);
        verify(modelMapper, times(1)).map(itemRequest, ItemRequestDtoWithoutResponses.class);
    }

    @Test
    void getRequestShouldGetRequestWithUserIdAndRequestId() {
        Long requestId = 1L;
        Long userId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        List<ItemResponseDto> items = List.of();
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(modelMapper.map(itemRequest, ItemRequestDto.class)).thenReturn(itemRequestDto);
        when(itemService.getItemsByRequestId(requestId)).thenReturn(items);

        ItemRequestDto obtainedDto = itemRequestService.getRequest(requestId, userId);

        assertThat(obtainedDto.getItems()).isEqualTo(items);
        verify(itemRequestRepository, times(1)).findById(requestId);
        verify(modelMapper, times(1)).map(itemRequest, ItemRequestDto.class);
        verify(itemService, times(1)).getItemsByRequestId(requestId);
    }

    @Test
    void getRequestShouldThrowNotFoundExceptionWhenRequestIdIsNonExistent() {
        Long requestId = 1L;
        Long userId = 1L;
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getRequest(requestId, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(String.format("request with id %d not found!", requestId));

        verify(itemRequestRepository, times(1)).findById(requestId);
        verifyNoInteractions(modelMapper);
        verifyNoInteractions(itemService);
    }

    @Test
    void getMyRequestsShouldGetRequests() {
        Long itemRequestDtoId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        List<ItemRequest> itemRequests = List.of(itemRequest);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequestDtoId);
        List<ItemResponseDto> itemResponseDtos = List.of();
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(itemRequests);
        when(modelMapper.map(itemRequest, ItemRequestDto.class)).thenReturn(itemRequestDto);
        when(itemService.getItemsByRequestId(itemRequestDtoId)).thenReturn(itemResponseDtos);

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getMyRequests(anyLong());

        assertThat(itemRequestDtos).size().isEqualTo(1);
        assertThat(itemRequestDtos.getFirst().getItems()).isEqualTo(itemResponseDtos);
        verify(itemRequestRepository, times(1)).findByRequestorIdOrderByCreatedDesc(anyLong());
        verify(modelMapper, times(1)).map(itemRequest, ItemRequestDto.class);
        verify(itemService, times(1)).getItemsByRequestId(itemRequestDtoId);
    }

    @Test
    void getMyRequestsShouldGetRequestsWithoutResponses() {
        Long itemRequestDtoId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        List<ItemRequest> itemRequests = List.of(itemRequest);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequestDtoId);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(itemRequests);
        when(modelMapper.map(itemRequest, ItemRequestDto.class)).thenReturn(itemRequestDto);
        when(itemService.getItemsByRequestId(itemRequestDtoId)).thenReturn(null);

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getMyRequests(anyLong());

        assertThat(itemRequestDtos).size().isEqualTo(1);
        assertThat(itemRequestDtos.getFirst().getItems()).isNull();
        verify(itemRequestRepository, times(1)).findByRequestorIdOrderByCreatedDesc(anyLong());
        verify(modelMapper, times(1)).map(itemRequest, ItemRequestDto.class);
        verify(itemService, times(1)).getItemsByRequestId(itemRequestDtoId);
    }

    @Test
    void getAllRequestsShouldGetAllRequests() {
        ItemRequest itemRequest = new ItemRequest();
        List<ItemRequest> itemRequests = List.of(itemRequest);
        ItemRequestDtoWithoutResponses itemRequestDtoWithoutResponses = new ItemRequestDtoWithoutResponses();
        when(itemRequestRepository.findAllNotOwnerRequestsSortedByCreatedTimeDesc(anyLong())).thenReturn(itemRequests);
        when(modelMapper.map(itemRequest, ItemRequestDtoWithoutResponses.class))
                .thenReturn(itemRequestDtoWithoutResponses);

        List<ItemRequestDtoWithoutResponses> itemRequestDtoWithoutResponsesList =
                itemRequestService.getAllRequests(anyLong());

        assertThat(itemRequestDtoWithoutResponsesList).size().isEqualTo(1);
        assertThat(itemRequestDtoWithoutResponsesList.getFirst()).isEqualTo(itemRequestDtoWithoutResponses);
        verify(itemRequestRepository, times(1)).findAllNotOwnerRequestsSortedByCreatedTimeDesc(anyLong());
        verify(modelMapper, times(1)).map(itemRequest, ItemRequestDtoWithoutResponses.class);
    }
}