package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.valid.Mark.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constant.Constants.USER_HEADER_ID;

@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto,
                                             @RequestHeader(USER_HEADER_ID) @Positive long userId) {
        log.info("Запрос на создание запроса вещи от юзера id = {}", userId);
        return requestClient.addRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsersItemRequest(@RequestHeader(USER_HEADER_ID) @Positive long userId) {
        log.info("Запрос на возврат списка запросов юзера: {}", userId);
        return requestClient.getAllUsersItemRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOtherUsersItemRequest(
            @RequestHeader(USER_HEADER_ID) @Positive long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Запрос от юзера id = {}, параметр from = {}, параметр size = {}", userId, from, size);
        return requestClient.getAllOtherUsersItemRequest(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getItemRequest(@PathVariable @Positive long requestId,
                                                 @RequestHeader(USER_HEADER_ID) @Positive long userId) {
        log.info("Запрос на возврат запроса с id = {}, от юзера id = {}", requestId, userId);
        return requestClient.getItemRequest(userId, requestId);
    }
}
