package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingTimeDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.*;
import ru.practicum.shareit.error.exception.NotAvailableException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.UnauthorizedException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto save(Long ownerId, ItemRequest itemRequest) {
        try {
            Item item = modelMapper.map(itemRequest, Item.class);
            item.setOwnerId(ownerId);
            return itemMapper.toDto(itemRepository.save(item));
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("User with id " + ownerId + " does not exist.");
        }
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemUpdateRequest itemUpdateRequest) {
        return itemRepository.findById(itemId)
                .map(item -> {
                    checkOwner(item, ownerId);
                    return itemMapper.toDto(itemRepository.save(updateItemFields(item, itemUpdateRequest)));
                })
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));
    }

    private void checkOwner(Item item, Long ownerId) {
        if (!item.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedException("Only the owner can update this item");
        }
    }

    private Item updateItemFields(Item itemToUpdate, ItemUpdateRequest itemUpdateRequest) {
        if (itemUpdateRequest.getName() != null) {
            itemToUpdate.setName(itemUpdateRequest.getName());
        }
        if (itemUpdateRequest.getDescription() != null) {
            itemToUpdate.setDescription(itemUpdateRequest.getDescription());
        }
        if (itemUpdateRequest.getAvailable() != null) {
            itemToUpdate.setAvailable(itemUpdateRequest.getAvailable());
        }
        return itemToUpdate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(Long ownerId, String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItemsByText(text).stream()
                .map(itemMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto get(Long ownerId, Long itemId) {
        Item item = itemRepository.findItemByIdWithComments(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id %d and ownerId %d does not exist.", itemId, ownerId)));

        ItemDto itemDto = itemMapper.toDto(item);
        setLastAndNextBooking(itemDto);
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItemsFromOwner(Long ownerId) {
        List<Item> items = itemRepository.findItemsByOwnerIdWithComments(ownerId);

        return items.stream()
                .map(item -> {
                    ItemDto itemDto = itemMapper.toDto(item);
                    setLastAndNextBooking(itemDto);
                    return itemDto;
                })
                .toList();
    }

    private void setLastAndNextBooking(ItemDto itemDto) {
        List<Booking> bookings = bookingRepository.getBookingsByItemId(itemDto.getId());

        if (bookings.size() > 1) {
            BookingTimeDto lastBooking = bookings.stream()
                    .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                    .max(Comparator.comparing(Booking::getEnd))
                    .map(booking -> modelMapper.map(booking, BookingTimeDto.class))
                    .orElseGet(null);

            BookingTimeDto nextBooking = bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getStart))
                    .map(booking -> modelMapper.map(booking, BookingTimeDto.class))
                    .findFirst().orElseGet(null);

            itemDto.setLastBooking(lastBooking);
            itemDto.setNextBooking(nextBooking);
        }
    }

    @Override
    public CommentDto saveComment(Long bookerId, Long itemId, CommentRequest commentRequest) {
        Comment comment = new Comment();
        comment.setText(commentRequest.getText());
        comment.setCreated(LocalDateTime.now());

        Booking booking =
                bookingRepository.getBookingByItemIdAndBookerIdAndEndBefore(itemId, bookerId, LocalDateTime.now())
                        .orElseThrow(() -> new NotAvailableException(
                                String.format("Booking with bookerId %d and itemId %d not found.", bookerId, itemId)));

        comment.setItem(booking.getItem());
        comment.setAuthor(booking.getBooker());

        return commentMapper.toDto(commentRepository.save(comment));
    }
}