package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.MarkerValidate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    private Long id;
//    @NotNull(groups = MarkerValidate.OnCreate.class)
    private String description;
    private User requestor;
    private LocalDateTime created;
    private List<ItemDto> items;
}
