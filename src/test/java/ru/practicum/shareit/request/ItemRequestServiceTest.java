package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.ex.ItemRequestNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @InjectMocks
    private ItemRequestService itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestStorage;

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemStorage;

    @Test
    public void addRequestTest() {
        long userId = 1;
        LocalDateTime dateTime = LocalDateTime.now();
        ItemRequest itemRequest = ItemRequest.builder()
                .description("Нужен телефон.")
                .created(dateTime)
                .requestor(User.builder()
                        .id(userId).build())
                .build();
        ItemRequestDto expectedItemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        when(userService.getUserById(userId)).thenReturn(UserDto.builder().id(userId).build());
        when(itemRequestStorage.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto actualItemRequestDto = itemRequestService
                .addRequest(expectedItemRequestDto, userId);

        assertEquals("check", expectedItemRequestDto, actualItemRequestDto);
        verify(userService, times(1)).checkExistsUser(userId);
    }

    @Test
    public void getAllItemRequestTest() {
        long userId = 1;
        when(itemRequestStorage.findAllByRequestorId(userId)).thenReturn(List.of(new ItemRequest()));

        List<ItemRequestDto> actualListItemRequest = itemRequestService.getAllItemRequest(userId);

        assertEquals("check",1, actualListItemRequest.size());
    }

    @Test
    public void getAllOtherUsersItemRequest() {
        long userId = 1;
        int from = 5;
        int size = 5;

        when(itemRequestStorage.findAllByRequestorIdIsNotOrderByCreated(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(new ItemRequest()));

        List<ItemRequestDto> actualListItemRequest =
                itemRequestService.getAllOtherUsersItemRequest(userId, from, size);

        assertEquals("check",1, actualListItemRequest.size());
    }

    @Test
    public void getItemRequest_whenFoundItemRequest_test() {
        long requestId = 1;
        long userId = 1;

        when(itemRequestStorage.findById(requestId)).thenReturn(Optional.of(ItemRequest.builder().id(1).build()));
        when(itemStorage.findByItemRequestId(requestId)).thenReturn(List.of(new Item()));

        ItemRequestDto actualListItemREquestDto = itemRequestService.getItemRequest(requestId, userId);

        assertEquals("check", requestId, actualListItemREquestDto.getId());
    }

    @Test
    public void getItemRequest_whenNotFoundItemRequest_test() {
        long requestId = 1;
        long userId = 1;

        when(itemRequestStorage.findById(requestId)).thenReturn(Optional.empty());
        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getItemRequest(requestId, userId));
    }
}
