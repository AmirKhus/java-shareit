package ru.practicum.shareit.user.entity;

import lombok.*;
import ru.practicum.shareit.MarkerValidate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(groups = MarkerValidate.OnCreate.class)
    @Column(name = "name", nullable = false)
    private String name;
    @NotBlank(groups = MarkerValidate.OnCreate.class)
    @Column(name = "email", unique = true)
    private String email;
}
