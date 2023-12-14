package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemStorage;
    private final UserService userService;
    private final CommentsRepository commentStorage;
    private final BookingRepository bookingStorage;
    private final ItemRequestService itemRequestService;
    private final ItemRequestRepository itemRequestStorage;

    @Transactional
    public ItemDto createItem(ItemDto itemDto, long userId) {
        Item item = ItemMapper.toItem(itemDto);
        User owner = UserMapper.toUser(userService.getUserById(userId));
        item.setOwner(owner);
        Item itemSave;

        if (itemDto.getItemRequest() != null) {
            ItemRequest itemRequest = itemRequestService.checkExistsItemRequests(itemDto.getItemRequest());
            item.setItemRequest(itemRequest);
            itemSave = itemStorage.save(item);
            itemRequestStorage.save(itemRequest);
        } else {
            itemSave = itemStorage.save(item);
        }

        return ItemMapper.toItemDto(itemSave);
    }

    @Transactional
    public ItemDto updateItem(ItemDto itemDto, long userId, long itemId) {

        checkExistItem(itemId);
        Item itemFromBd = itemStorage.getReferenceById(itemId);

        if (!(itemFromBd.getOwner().getId() == userId)) {
            throw new CheckUserException("Отсутствует доступ к вещи id = " + itemId);
        }

        if (itemDto.getName() != null) {
            itemFromBd.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            itemFromBd.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            itemFromBd.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemStorage.save(itemFromBd));
    }

    public ItemDto getItemById(long itemId, long userId) {
        checkExistItem(itemId);
        userService.checkExistsUser(userId);

        List<Item> items = new ArrayList<>();
        Item item = itemStorage.getReferenceById(itemId);
        items.add(item);

        List<CommentDto> comments = commentStorage.findByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

         if (item.getOwner().getId() == userId) {
            ItemOwnerDto itemOwnerDto = buildListItemOwnerDto(items).get(0);
            itemOwnerDto.setComments(comments);
            return ItemMapper.toItemDto(itemOwnerDto);
        } else {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            itemDto.setComments(comments);
            return itemDto;
        }
    }

    public List<ItemOwnerDto> findAllOwnersItems(long userId, int from, int size) {
        userService.checkExistsUser(userId);
        List<Item> items = itemStorage.findByOwnerId(userId, Page.createPageRequest(from, size));
        return buildListItemOwnerDto(items);
    }

    private List<ItemOwnerDto> buildListItemOwnerDto(List<Item> items) {
        List<ItemOwnerDto> itemOwnerDtos = new ArrayList<>();

        for (Item item : items) {
            ItemOwnerDto itemOwnerDto = ItemMapper.toItemOwnerDto(item);
            List<Booking> rawBookings =
                    bookingStorage.findByItemIdAndStatusIsNot(item.getId(), BookingStatus.REJECTED);

            List<Booking> bookings = rawBookings.stream()
                    .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                    .sorted((x, x1) -> x.getStart().compareTo(x1.getStart()))
                    .collect(Collectors.toList());

            if (bookings.size() != 0) {
                itemOwnerDto.setNextBooking(BookingMapper.toBookingOwnerDto(bookings.get(0)));
            }

            bookings = rawBookings.stream()
                    .filter(x -> x.getEnd().isAfter(LocalDateTime.now()) &&
                                 x.getStart().isBefore(LocalDateTime.now()))
                    .sorted((x, x1) -> x1.getEnd().compareTo(x.getEnd()))
                    .collect(Collectors.toList());

            if (bookings.size() == 0) {
                bookings = rawBookings.stream()
                        .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                        .sorted((x, x1) -> x1.getEnd().compareTo(x.getEnd()))
                        .collect(Collectors.toList());
            }

            if (bookings.size() != 0) {
                itemOwnerDto.setLastBooking(BookingMapper.toBookingOwnerDto(bookings.get(0)));
            }

            itemOwnerDtos.add(itemOwnerDto);
        }

        return itemOwnerDtos.stream()
                .sorted(Comparator.comparingLong(ItemOwnerDto::getId))
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItem(String text, int from, int size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemStorage.search(text.toLowerCase().trim(), Page.createPageRequest(from, size))
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public boolean checkIsAvailableItem(long itemId) {
        Optional<Item> optionalItem = itemStorage.findById(itemId);

        if (optionalItem.isEmpty()) {
            throw new ItemNotFoundException("Отсутствует вещь с id = " + itemId);
        }

        return optionalItem.get().getAvailable();
    }

    public Item getItem(long itemId) {
        checkExistItem(itemId);
        return itemStorage.getReferenceById(itemId);
    }

    @Transactional
    public CommentDto addComment(CommentDto commentDto, long itemId, long userId) {
        checkExistItem(itemId);
        userService.checkExistsUser(userId);
        Optional<List<Booking>> bookings = bookingStorage.getBookingItemWhichTookUser(itemId, userId);

        if (bookings.isEmpty() || bookings.get().size() == 0) {
            throw new NoAccessCommentException("Нельзя оставить комментарий вещи, не взяв её в аренду");
        }

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(itemStorage.getReferenceById(itemId));
        comment.setAuthor(UserMapper.toUser(userService.getUserById(userId)));
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentStorage.save(comment));
    }

    private void checkExistItem(long itemId) {
        if (itemStorage.findById(itemId).isEmpty()) {
            throw new ItemNotFoundException("Отсутствует вещь с id = " + itemId);
        }
    }
}
