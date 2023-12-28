package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .itemRequest(item.getItemRequest() != null ? item.getItemRequest().getId() : null)
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemOwnerDto toItemOwnerDto(Item item) {
        return ItemOwnerDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemDto toItemDto(ItemOwnerDto itemOwnerDto) {
        log.info("lastBook равен " + itemOwnerDto.getLastBooking());
        return ItemDto.builder()
                .id(itemOwnerDto.getId())
                .name(itemOwnerDto.getName())
                .description(itemOwnerDto.getDescription())
                .available(itemOwnerDto.getAvailable())
                .nextBooking(itemOwnerDto.getNextBooking())
                .lastBooking(itemOwnerDto.getLastBooking())
                .comments(itemOwnerDto.getComments())
                .build();
    }
}
