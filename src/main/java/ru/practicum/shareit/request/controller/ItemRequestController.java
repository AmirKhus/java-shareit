package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.constatnt.Constants.USER_HEADER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                     @RequestHeader(USER_HEADER_ID) long userId) {
        log.info("Запрос на создание запроса вещи");
        return itemRequestService.addRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllUsersItemRequest(@RequestHeader(USER_HEADER_ID) long userId) {
        log.info("Запрос на возврат списка запросов юзера: " + userId);
        return itemRequestService.getAllItemRequest(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllOtherUsersItemRequest(
            @RequestHeader(USER_HEADER_ID) long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info(String.format("Запрос от юзера id = %d, " +
                "параметр from = %d, параметр size = %d", userId, from, size));
        return itemRequestService.getAllOtherUsersItemRequest(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getItemRequest(@PathVariable long requestId,
                                         @RequestHeader(USER_HEADER_ID) long userId) {
        log.info("Запрос на возврат запроса с id = " + requestId + " от юзера id = " + userId);
        return itemRequestService.getItemRequest(requestId, userId);
    }
}
