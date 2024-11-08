package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.*;
import ru.practicum.shareit.error.exception.NotAvailableException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.UnauthorizedException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository; // useless?
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;

    //нужно ли проверять наличия ownerId при добавлении item
    @Override
    public ItemDto save(Long ownerId, ItemRequest itemRequest) {
        try {
            Item item = modelMapper.map(itemRequest, Item.class);
            item.setOwnerId(ownerId);
            return modelMapper.map(itemRepository.save(item), ItemDto.class);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("User with id " + ownerId + " does not exist.");
        }
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemUpdateRequest itemUpdateRequest) {
        return itemRepository.findById(itemId)
                .map(item -> {
                    checkOwner(item, ownerId);
                    return modelMapper.map(itemRepository.save(updateItemFields(item, itemUpdateRequest)), ItemDto.class);
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
                .map(item -> modelMapper.map(item, ItemDto.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto get(Long ownerId, Long itemId) {
        Item item = itemRepository.findItemByIdWithComments(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id %d and ownerId %d does not exist.", itemId, ownerId)));

        Optional<Booking> lastBooking = bookingRepository.getLastBookingById(itemId);
        Optional<Booking> nextBooking = bookingRepository.getNextBookingById(itemId);

        ItemDto itemDto = modelMapper.map(item, ItemDto.class);

        lastBooking.ifPresent(booking -> itemDto.setLastBooking(modelMapper.map(booking, BookingDto.class)));
        nextBooking.ifPresent(booking -> itemDto.setNextBooking(modelMapper.map(booking, BookingDto.class)));
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItemsFromOwner(Long ownerId) {
        List<Item> items = itemRepository.findItemsByOwnerIdWithComments(ownerId);

        return items.stream()
                .map(item -> {
                    Optional<Booking> lastBooking = bookingRepository.getLastBookingById(item.getId());
                    Optional<Booking> nextBooking = bookingRepository.getNextBookingById(item.getId());

                    ItemDto itemDto = modelMapper.map(item, ItemDto.class);

                    lastBooking.ifPresent(booking -> itemDto.setLastBooking(modelMapper.map(booking, BookingDto.class)));
                    nextBooking.ifPresent(booking -> itemDto.setNextBooking(modelMapper.map(booking, BookingDto.class)));

                    return itemDto;
                })
                .toList();
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

        return CommentMapper.toDto(commentRepository.save(comment));
    }
}