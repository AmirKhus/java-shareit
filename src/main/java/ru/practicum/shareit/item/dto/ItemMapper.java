package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();

        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }

        return itemDto;
    }

    public static ItemDto toEntityItemDto(ru.practicum.shareit.item.entity.Item item) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();

        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }

        return itemDto;
    }

    public static Item fromItemDto(ItemDto itemDto, User user) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .user(user)
                .build();
    }


    public static ru.practicum.shareit.item.entity.Item fromEntityItemDto(ItemDto itemDto, ru.practicum.shareit.user.entity.User user) {
        return ru.practicum.shareit.item.entity.Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .user(user)
                .build();
    }

    public static List<ItemDto> toItemDtoList(Iterable<ru.practicum.shareit.item.entity.Item> items) {
        List<ItemDto> result = new ArrayList<>();

        for (ru.practicum.shareit.item.entity.Item item : items) {
            result.add(toEntityItemDto(item));
        }
        return result;
    }
}
