package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.comment.Comment;

import java.util.List;

@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
    @OneToMany(mappedBy = "item")
    private List<Comment> comments;
}