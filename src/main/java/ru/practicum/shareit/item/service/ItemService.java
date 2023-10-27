package ru.practicum.shareit.item.service;

import ru.practicum.shareit.BaseService;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService extends BaseService<ItemDto> {
    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    List<ItemDto> getItemByUser(Long userId);

    List<ItemDto> search(String query);
}
