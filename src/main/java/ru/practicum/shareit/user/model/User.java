package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Column(name = "user_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_name", nullable = false)
    @NotBlank(message = "Имя не должно быть пустым.")
    private String name;

    @Column(name = "user_email", nullable = false, unique = true)
    @NotBlank(message = "Отсутствует email.")
    @Email(message = "Введен некорректный адрес электронной почты.")
    private String email;
}
