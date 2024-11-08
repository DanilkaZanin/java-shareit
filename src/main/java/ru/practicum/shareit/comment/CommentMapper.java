package ru.practicum.shareit.comment;

public class CommentMapper {

    private CommentMapper() {
        throw new IllegalStateException("Mapper");
    }

    public static CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreated());
        return dto;
    }
}