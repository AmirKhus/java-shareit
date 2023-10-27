package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.MarkerValidate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDto> getAllItemsByUser(@RequestHeader(HEADER_USER_ID) Long userId) {
        return itemService.getItemByUser(userId);
    }

    @GetMapping("{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping("search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(HEADER_USER_ID) Long userId,
                              @Validated({MarkerValidate.OnCreate.class}) @NotNull @RequestBody ItemDto itemDto) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER_USER_ID) Long userId,
                              @PathVariable Long itemId,
                              @Validated(MarkerValidate.OnUpdate.class) @NotNull @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public ItemDto deleteItem(@PathVariable Long itemId) {
        return itemService.delete(itemId);
    }
}