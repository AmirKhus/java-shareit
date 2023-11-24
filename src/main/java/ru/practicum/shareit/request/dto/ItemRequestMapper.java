package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto returnItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(null)
                .build();
        return itemRequestDto;
    }

    public static ItemRequest returnItemRequest(ItemRequestDto itemRequestDto, User user) {
        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .requester(user)
                .build();
        return itemRequest;
    }

    public static List<ItemRequestDto> returnItemRequestDtoList(Iterable<ItemRequest> requests) {
        List<ItemRequestDto> result = new ArrayList<>();

        for (ItemRequest itemRequest : requests) {
            result.add(returnItemRequestDto(itemRequest));
        }
        return result;
    }
}
