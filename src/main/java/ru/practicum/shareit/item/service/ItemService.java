package ru.practicum.shareit.item.service;

import ru.practicum.shareit.BaseService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService extends BaseService<ItemDto> {
    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    List<ItemDto> getItemByUser(Long userId, Integer from, Integer size);

    List<ItemDto> search(String text, Integer from, Integer size);

    ItemDto getItemById(Long itemId, Long userId);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);

    List<ItemDto> getItemsUser(Long userId, Integer from, Integer size);
}
