package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.MarkerValidate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotBlank(groups = MarkerValidate.OnCreate.class)
    private String name;
    @Email(groups = MarkerValidate.OnCreate.class)
    @NotBlank(groups = MarkerValidate.OnCreate.class)
    private String email;
}
