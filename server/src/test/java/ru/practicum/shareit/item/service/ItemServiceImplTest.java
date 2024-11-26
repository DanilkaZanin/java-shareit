package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.UnauthorizedException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.map.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void shouldSaveItem() {
        Long ownerId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        Item item = new Item();
        item.setOwnerId(ownerId);
        Item savedItem = new Item();
        ItemDto expectedItemDto = new ItemDto();

        when(modelMapper.map(itemRequest, Item.class)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(savedItem);
        when(itemMapper.toDto(savedItem)).thenReturn(expectedItemDto);

        ItemDto actualItemDto = itemService.save(ownerId, itemRequest);

        assertThat(actualItemDto).isEqualTo(expectedItemDto);
        verify(modelMapper, times(1)).map(itemRequest, Item.class);
        verify(itemRepository, times(1)).save(item);
        verify(itemMapper, times(1)).toDto(savedItem);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenSaveFails() {
        Long ownerId = 1L;
        ItemRequest itemRequest = new ItemRequest();

        when(modelMapper.map(itemRequest, Item.class)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> itemService.save(ownerId, itemRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with id " + ownerId + " does not exist.");

        verify(itemRepository, never()).save(any());
    }

    @Test
    void shouldUpdateItem() {
        Long ownerId = 1L;
        Long itemId = 1L;
        ItemUpdateRequest updateRequest = new ItemUpdateRequest();
        Item existingItem = new Item();
        existingItem.setOwnerId(ownerId);
        Item updatedItem = new Item();
        ItemDto expectedItemDto = new ItemDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(existingItem)).thenReturn(updatedItem);
        when(itemMapper.toDto(updatedItem)).thenReturn(expectedItemDto);

        ItemDto actualItemDto = itemService.update(ownerId, itemId, updateRequest);

        assertThat(actualItemDto).isEqualTo(expectedItemDto);
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).save(existingItem);
        verify(itemMapper, times(1)).toDto(updatedItem);
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenUpdatingOtherOwnersItem() {
        Long ownerId = 1L;
        Long itemId = 1L;
        ItemUpdateRequest updateRequest = new ItemUpdateRequest();
        Item existingItem = new Item();
        existingItem.setOwnerId(2L);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        assertThatThrownBy(() -> itemService.update(ownerId, itemId, updateRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Only the owner can update this item");

        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemToUpdateNotFound() {
        Long ownerId = 1L;
        Long itemId = 1L;
        ItemUpdateRequest updateRequest = new ItemUpdateRequest();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.update(ownerId, itemId, updateRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Item with id " + itemId + " not found");

        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void shouldSearchItems() {
        String searchText = "keyword";
        Item item1 = new Item();
        Item item2 = new Item();
        ItemDto itemDto1 = new ItemDto();
        ItemDto itemDto2 = new ItemDto();

        when(itemRepository.searchItemsByText(searchText)).thenReturn(List.of(item1, item2));
        when(itemMapper.toDto(item1)).thenReturn(itemDto1);
        when(itemMapper.toDto(item2)).thenReturn(itemDto2);

        List<ItemDto> result = itemService.search(1L, searchText);

        assertThat(result).hasSize(2).containsExactly(itemDto1, itemDto2);
        verify(itemRepository, times(1)).searchItemsByText(searchText);
    }

    @Test
    void shouldReturnEmptyListWhenSearchTextIsEmpty() {
        List<ItemDto> result = itemService.search(1L, "");

        assertThat(result).isEmpty();
        verifyNoInteractions(itemRepository);
    }

    @Test
    void shouldGetAllItemsFromOwnerWithBookings() {
        Long ownerId = 1L;
        Item item1 = new Item();
        Item item2 = new Item();
        ItemDto itemDto1 = new ItemDto();
        ItemDto itemDto2 = new ItemDto();
        List<Item> items = List.of(item1, item2);

        when(itemRepository.findItemsByOwnerIdWithComments(ownerId)).thenReturn(items);
        when(itemMapper.toDto(item1)).thenReturn(itemDto1);
        when(itemMapper.toDto(item2)).thenReturn(itemDto2);

        List<ItemDto> result = itemService.getAllItemsFromOwner(ownerId);

        assertThat(result).hasSize(2).containsExactly(itemDto1, itemDto2);
        verify(itemRepository, times(1)).findItemsByOwnerIdWithComments(ownerId);
        verify(itemMapper, times(2)).toDto(any(Item.class));
    }

    @Test
    void shouldGetItemByIdWithBookings() {
        Long ownerId = 1L;
        Long itemId = 1L;
        Item item = new Item();
        ItemDto itemDto = new ItemDto();

        when(itemRepository.findItemByIdWithComments(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.get(ownerId, itemId);

        assertThat(result).isEqualTo(itemDto);
        verify(itemRepository, times(1)).findItemByIdWithComments(itemId);
        verify(itemMapper, times(1)).toDto(item);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFound() {
        Long ownerId = 1L;
        Long itemId = 1L;

        when(itemRepository.findItemByIdWithComments(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.get(ownerId, itemId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Item with id " + itemId + " and ownerId " + ownerId + " does not exist.");

        verify(itemRepository, times(1)).findItemByIdWithComments(itemId);
    }

    @Test
    void shouldReturnEmptyListWhenSearchTextIsNull() {
        List<ItemDto> result = itemService.search(1L, null);

        assertThat(result).isEmpty();
        verifyNoInteractions(itemRepository);
    }

    @Test
    void shouldGetItemsByRequestId() {
        Long requestorId = 1L;
        List<ItemResponseDto> itemResponseDtos1 = new ArrayList<>();

        when(itemRepository.findItemsByRequestId(requestorId)).thenReturn(itemResponseDtos1);

        List<ItemResponseDto> itemResponseDtos2 = itemService.getItemsByRequestId(requestorId);

        assertThat(itemResponseDtos1).isEqualTo(itemResponseDtos2);
        verify(itemRepository, times(1)).findItemsByRequestId(requestorId);
    }

}