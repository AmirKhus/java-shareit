package ru.practicum.shareit.item.entity;

import lombok.*;
import ru.practicum.shareit.MarkerValidate;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.entity.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "items")
public class Item implements Serializable {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    @NotBlank(groups = {MarkerValidate.OnCreate.class})
    private String name;
    @NotBlank(groups = {MarkerValidate.OnCreate.class})
    private String description;
    @NotNull(groups = {MarkerValidate.OnCreate.class})
    @Column(name = "is_available")
    private Boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}
