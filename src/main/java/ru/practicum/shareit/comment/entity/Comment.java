package ru.practicum.shareit.comment.entity;

import lombok.*;
import ru.practicum.shareit.MarkerValidate;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Comment {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @NotNull(groups = {MarkerValidate.OnCreate.class})
    private String text;
    private LocalDateTime created;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
}
