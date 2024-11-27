package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;

@Controller
@Slf4j
@RequestMapping(value = "items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                           @RequestBody @Valid ItemRequest item) {
        log.info("Saving item {} with owner id {}", item, ownerId);
        return itemClient.saveItem(ownerId, item);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("itemId") long itemId,
                                             @RequestBody @Valid ItemUpdateRequest item) {
        log.info("Updating item {} with owner id {}", itemId, userId);
        return itemClient.updateItem(userId, itemId, item);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable("itemId") long itemId) {
        log.info("Retrieving item {} with owner id {}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Retrieving items with owner id {}", userId);
        return itemClient.getAllItemsFromOwner(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @NotBlank @RequestParam String text) {
        log.info("Retrieving items with text {} and user id {}", text, userId);
        return itemClient.searchItem(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable("itemId") long itemId,
                                              @RequestBody @Valid CommentRequest commentRequest) {
        return itemClient.saveComment(userId, itemId, commentRequest);
    }

}
