package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class ItemMapperImpl implements ItemMapper {
    private final ModelMapper modelMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto toDto(Item item) {
        ItemDto itemDto = new ItemDto();
        modelMapper.map(item, itemDto);
        itemDto.setComments(mapComments(item.getComments()));

        return itemDto;
    }

    private List<CommentDto> mapComments(List<Comment> comments) {
        if (comments == null) {
            return Collections.emptyList();
        }

        return comments.stream()
                .map(commentMapper::toDto).toList();
    }
}