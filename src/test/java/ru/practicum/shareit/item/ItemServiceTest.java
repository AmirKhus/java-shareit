package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ex.CheckUserException;
import ru.practicum.shareit.exceptions.ex.ItemNotFoundException;
import ru.practicum.shareit.exceptions.ex.NoAccessCommentException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentsRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private UserService userService;

    @Mock
    private ItemRequestRepository itemRequestStorage;

    @Mock
    private ItemRepository itemStorage;

    @Mock
    private ItemRequestService itemRequestService;

    @Mock
    private CommentsRepository commentsStorage;

    @Mock
    private BookingRepository bookingStorage;

    @Mock
    private UserRepository userStorage;

    @Test
    public void createItem_withoutItemRequest_test() {
        long userId = 1;
        Item item = Item.builder()
                .id(1)
                .build();
        ItemDto expectedItemDto = ItemMapper.toItemDto(item);
        when(userService.getUserById(userId)).thenReturn(UserDto.builder()
                .id(userId)
                .build());
        when(itemStorage.save(any(Item.class))).thenReturn(item);

        ItemDto actualItemDto = itemService.createItem(expectedItemDto, userId);

        assertEquals("check", expectedItemDto, actualItemDto);
        verify(itemRequestStorage, never()).save(any(ItemRequest.class));
    }

    @Test
    public void createItem_withItemRequest_test() {
        long userId = 1;
        Item item = Item.builder()
                .id(1)
                .itemRequest(ItemRequest.builder()
                        .id(1)
                        .build())
                .build();
        ItemDto expectedItemDto = ItemMapper.toItemDto(item);
        when(userService.getUserById(userId)).thenReturn(UserDto.builder().id(userId).build());
        when(itemStorage.save(any(Item.class))).thenReturn(item);
        when(itemRequestService.checkExistsItemRequests(1)).thenReturn(ItemRequest.builder()
                .id(1)
                .build());

        ItemDto actualItemDto = itemService.createItem(expectedItemDto, userId);

        assertEquals("check", expectedItemDto, actualItemDto);
        verify(itemRequestStorage, times(1)).save(any(ItemRequest.class));
    }

    @Test
    public void updateItem_whenUserOwner_test() {
        long userId = 1;
        long itemId = 1;
        Item item = Item.builder()
                .id(itemId)
                .name("Name")
                .description("Description")
                .available(false)
                .owner(User.builder()
                        .id(itemId)
                        .build())
                .build();

        ItemDto expectedItemDto = ItemMapper.toItemDto(item);

        when(itemStorage.save(item)).thenReturn(item);
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(itemStorage.getReferenceById(itemId)).thenReturn(item);

        ItemDto actualItemDto = itemService.updateItem(expectedItemDto, itemId, userId);

        assertEquals("check", expectedItemDto, actualItemDto);
    }

    @Test
    public void updateItem_whenUserNotOwner_test() {
        long userId = 2;
        long itemId = 1;
        Item item = Item.builder()
                .id(itemId)
                .name("Name")
                .description("Description")
                .available(false)
                .owner(User.builder()
                        .id(1)
                        .build())
                .build();

        ItemDto expectedItemDto = ItemMapper.toItemDto(item);

        when(itemStorage.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(itemStorage.getReferenceById(itemId)).thenReturn(item);

        assertThrows(CheckUserException.class, () -> itemService.updateItem(expectedItemDto, userId, itemId));
    }

    @Test
    public void getItemId_whenFoundItemTest() {
        long itemId = 1;
        Item item = Item.builder()
                .id(itemId)
                .name("Name")
                .description("Description")
                .available(false)
                .build();

        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        when(itemStorage.getReferenceById(itemId)).thenReturn(item);

        Item actualItem = itemService.getItem(itemId);

        assertEquals("check", item, actualItem);
    }

    @Test
    public void getItemId_whenNotFoundItemTest() {
        long itemId = 1;
        when(itemStorage.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.getItem(itemId));
    }

    @Test
    public void getItemById_whenUserOwnerTest() {
        long itemId = 1;
        long userId = 1;

        Item item = Item.builder()
                .id(itemId)
                .name("Name")
                .description("Description")
                .available(false)
                .owner(User.builder()
                        .id(1)
                        .build())
                .build();

        ItemDto expectedItem = ItemMapper.toItemDto(item);
        expectedItem.setComments(Collections.emptyList());

        when(itemStorage.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(itemStorage.getReferenceById(itemId)).thenReturn(item);
        when(commentsStorage.findByItemId(itemId)).thenReturn(Collections.emptyList());
        when(bookingStorage.findByItemIdAndStatusIsNot(itemId, BookingStatus.REJECTED))
                .thenReturn(Collections.emptyList());
        ItemDto actualItem = itemService.getItemById(itemId, userId);

        assertEquals("check", expectedItem, actualItem);
    }

    @Test
    public void getItemById_whenUserNotOwnerTest() {
        long itemId = 1;
        long userId = 2;

        Item item = Item.builder()
                .id(itemId)
                .name("Name")
                .description("Description")
                .available(false)
                .owner(User.builder()
                        .id(1)
                        .build())
                .build();

        ItemDto expectedItem = ItemMapper.toItemDto(item);
        expectedItem.setComments(Collections.emptyList());

        when(itemStorage.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(itemStorage.getReferenceById(itemId)).thenReturn(item);
        when(commentsStorage.findByItemId(itemId)).thenReturn(Collections.emptyList());
        ItemDto actualItem = itemService.getItemById(itemId, userId);

        assertEquals("check", expectedItem, actualItem);

        verify(bookingStorage, never()).findByItemIdAndStatusIsNot(itemId, BookingStatus.REJECTED);
    }

    @Test
    public void checkIsAvailableItem_whenFoundItemTest() {
        long itemId = 1;
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(Item.builder()
                .available(true)
                .build()));

        assertTrue("check", itemService.checkIsAvailableItem(itemId));
    }

    @Test
    public void checkIsAvailableItem_whenNotFoundItemTest() {
        long itemId = 1;
        when(itemStorage.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.checkIsAvailableItem(itemId));
    }

    @Test
    public void findAllOwnersItemsTest() {
        long userId = 1;
        int from = 1;
        int size = 1;

        when(itemStorage.findByOwnerId(userId, PageRequest.of(from, size))).thenReturn(List.of(new Item()));

        List<ItemOwnerDto> actualItemList = itemService.findAllOwnersItems(userId, from, size);

        assertEquals("check", 1, actualItemList.size());
    }

    @Test
    public void searchItem_whenTextIsBlankTest() {
        String text = " ";
        int from = 1;
        int size = 1;

        assertTrue("check", itemService.searchItem(text, from, size).isEmpty());
    }

    @Test
    public void searchItem_whenTextNotIsBlankTest() {
        String text = "камера";
        int from = 1;
        int size = 1;

        when(itemStorage.search(text, PageRequest.of(from, size))).thenReturn(List.of(new Item()));

        List<ItemDto> actualListItemDto = itemService.searchItem(text, from, size);

        assertEquals("check", 1, actualListItemDto.size());
    }

    @Test
    public void addComment_whenFoundBookingTest() {
        long itemId = 1;
        long userId = 1;

        Comment comment = Comment.builder()
                .id(1)
                .text("Хорошая камера")
                .item(new Item())
                .author(new User())
                .created(LocalDateTime.now())
                .build();

        CommentDto expectedCommentDto = CommentMapper.toCommentDto(comment);

        when(userService.getUserById(userId)).thenReturn(UserDto.builder()
                .id(1)
                .build());
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(bookingStorage.getBookingItemWhichTookUser(itemId, userId))
                .thenReturn(Optional.of(List.of(Booking.builder().build())));
        when(itemStorage.getReferenceById(itemId)).thenReturn(new Item());
        when(commentsStorage.save(any(Comment.class))).thenReturn(comment);

        CommentDto actualCommentDto = itemService.addComment(expectedCommentDto, itemId, itemId);

        assertEquals("check", expectedCommentDto, actualCommentDto);
    }

    @Test
    public void addComment_whenNotFoundBookingTest() {
        long itemId = 1;
        long userId = 1;

        CommentDto expectedCommentDto = CommentDto.builder()
                .text("Мобила")
                .build();

        when(itemStorage.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(bookingStorage.getBookingItemWhichTookUser(itemId, userId))
                .thenReturn(Optional.empty());

        assertThrows(NoAccessCommentException.class, () -> itemService.addComment(expectedCommentDto, itemId, itemId));
    }
}