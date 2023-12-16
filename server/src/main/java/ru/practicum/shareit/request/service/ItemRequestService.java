package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exceptions.ex.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestService {
    private final ItemRequestRepository itemRequestStorage;
    private final UserService userService;
    private final ItemRepository itemStorage;

    public ItemRequestDto addRequest(ItemRequestDto itemRequestDto, long userId) {
        userService.checkExistsUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(UserMapper.toUser(userService.getUserById(userId)));
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestStorage.save(itemRequest));
    }

    public List<ItemRequestDto> getAllItemRequest(long userId) {
        userService.checkExistsUser(userId);
        return getListItemRequestDto(itemRequestStorage.findAllByRequestorId(userId));
    }

    public List<ItemRequestDto> getAllOtherUsersItemRequest(long userId, int from, int size) {
        userService.checkExistsUser(userId);
        log.info("From = " + from + " Size = " + size);
        return getListItemRequestDto(itemRequestStorage
                .findAllByRequestorIdIsNotOrderByCreated(userId, Page.createPageRequest(from, size)));
    }

    private List<ItemRequestDto> getListItemRequestDto(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> listItemRequestDto = itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        for (ItemRequestDto itemRequestDto : listItemRequestDto) {
            itemRequestDto.setItems(itemStorage.findByItemRequestId(itemRequestDto.getId())
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList()));
        }

        return listItemRequestDto;
    }

    public ItemRequestDto getItemRequest(long requestId, long userId) {
        userService.checkExistsUser(userId);
        ItemRequest itemRequest = checkExistsItemRequests(requestId);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemStorage.findByItemRequestId(itemRequestDto.getId()).stream()
                                .map(ItemMapper::toItemDto)
                                .collect(Collectors.toList()));
        return itemRequestDto;
    }

    public ItemRequest checkExistsItemRequests(long itemRequestId) {
        Optional<ItemRequest> optionalItemRequest = itemRequestStorage.findById(itemRequestId);

        if (optionalItemRequest.isEmpty()) {
            throw new NotFoundException("Запроса с id " + itemRequestId + " не существует");
        }

        return optionalItemRequest.get();
    }
}