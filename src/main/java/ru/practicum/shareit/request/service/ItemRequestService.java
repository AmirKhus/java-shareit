package ru.practicum.shareit.request.service;

import ru.practicum.shareit.BaseService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.entity.ItemRequest;

import java.util.List;

public interface ItemRequestService{
    ItemRequestDto addRequest(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);

    List<ItemRequestDto> getRequests(long userId);

    ItemRequestDto getRequestById(long userId, long requestId);

    ItemRequestDto addItemsToRequest(ItemRequest itemRequest);
}
