package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.MarkerValidate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(groups = {MarkerValidate.OnCreate.class})
    private String name;
    @NotBlank(groups = {MarkerValidate.OnCreate.class})
    private String description;
    @NotNull(groups = {MarkerValidate.OnCreate.class})
    private Boolean available;
    private Long userId;
    private Long requestId;
}
