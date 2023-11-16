package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.entity.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingShortDto;
import static ru.practicum.shareit.comment.dto.CommentMapper.toCommentDtoList;
import static ru.practicum.shareit.item.dto.ItemMapper.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;


    @Override
    public ItemDto getById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item with id = " + id + " not found"));
        return ItemMapper.toEntityItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        checkUser(userId);

        User user = userRepository.findById(userId).get();
        Item item = fromEntityItemDto(itemDto, user);
        if (itemDto.getRequestId() != null) {
            if (!itemRequestRepository.existsById(itemDto.getRequestId())) {
                throw new NotFoundException("Request id " + itemDto.getRequestId() + " not found.");
            }
            item.setRequest(itemRequestRepository.findById(itemDto.getRequestId()).get());
        }
        itemRepository.save(item);

        return toEntityItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        checkUser(userId);

        User user = userRepository.findById(userId).get();

        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Item id " + itemId + " not found.");
        }
        Item item = fromEntityItemDto(itemDto, user);

        item.setId(itemId);

        if (!itemRepository.findByUserId(userId).contains(item)) {
            throw new NotFoundException("The item was not found with the user id " + userId);
        }

        Item newItem = itemRepository.findById(item.getId()).get();

        if (item.getName() != null) {
            newItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }

        itemRepository.save(newItem);

        return toEntityItemDto(newItem);
    }

    @Override
    public void delete(Long id) {
        itemRepository.delete(getItemById(id));
    }

    @Transactional
    @Override
    public List<ItemDto> getItemByUser(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<ItemDto> resultList = new ArrayList<>();

        for (ItemDto itemDto : toItemDtoList(itemRepository.findByUserIdOrderById(userId, pageRequest))) {

            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                    itemDto.getId(), Status.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                    itemDto.getId(), Status.APPROVED, LocalDateTime.now());

            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(toBookingShortDto(lastBooking.get()));
            } else {
                itemDto.setLastBooking(null);
            }

            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(toBookingShortDto(nextBooking.get()));
            } else {
                itemDto.setNextBooking(null);
            }

            resultList.add(itemDto);
        }

        for (ItemDto itemDto : resultList) {

            List<Comment> commentList = commentRepository.findAllByItemId(itemDto.getId());

            if (!commentList.isEmpty()) {
                itemDto.setComments(toCommentDtoList(commentList));
            } else {
                itemDto.setComments(Collections.emptyList());
            }
        }

        return resultList;
    }

    @Override
    @Transactional
    public List<ItemDto> search(String text, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        if (text.isEmpty()) {
            return Collections.emptyList();
        } else {
            return toItemDtoList(itemRepository.search(text, pageRequest));
        }
    }

    @Transactional
    @Override
    public ItemDto getItemById(Long itemId, Long userId) {

        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Item id " + itemId + " not found.");
        }
        Item item = itemRepository.findById(itemId).get();

        ItemDto itemDto = ItemMapper.toEntityItemDto(item);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User id " + userId + " not found.");
        }

        if (item.getUser().getId().equals(userId)) {

            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemId, Status.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemId, Status.APPROVED, LocalDateTime.now());

            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(toBookingShortDto(lastBooking.get()));
            } else {
                itemDto.setLastBooking(null);
            }

            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(toBookingShortDto(nextBooking.get()));
            } else {
                itemDto.setNextBooking(null);
            }
        }

        List<Comment> commentList = commentRepository.findAllByItemId(itemId);

        if (!commentList.isEmpty()) {
            itemDto.setComments(toCommentDtoList(commentList));
        } else {
            itemDto.setComments(Collections.emptyList());
        }

        return itemDto;
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("There is no user with id: " + userId));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("There is no user with id: " + itemId));
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = getUserById(userId);
        if (commentDto.getText().isEmpty())
            throw new BadRequestException("Пустое сообщение");
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Объект класса %s не найден", Item.class)));
        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(user.getId(), item.getId(), LocalDateTime.now())) {
            throw new BadRequestException("Пользователь не пользовался вещью");
        }
        Comment comment = commentRepository.save(CommentMapper.fromComment(commentDto, item, user, LocalDateTime.now()));
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<ItemDto> getItemsUser(Long userId, Integer from, Integer size) {
        checkUser(userId);

        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<ItemDto> resultList = new ArrayList<>();

        for (ItemDto itemDto : ItemMapper.toItemDtoList(itemRepository.findByUserIdOrderById(userId, pageRequest))) {

            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemDto.getId(), Status.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemDto.getId(), Status.APPROVED, LocalDateTime.now());

            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(toBookingShortDto(lastBooking.get()));
            } else {
                itemDto.setLastBooking(null);
            }

            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(toBookingShortDto(nextBooking.get()));
            } else {
                itemDto.setNextBooking(null);
            }

            resultList.add(itemDto);
        }

        for (ItemDto itemDto : resultList) {

            List<Comment> commentList = commentRepository.findAllByItemId(itemDto.getId());

            if (!commentList.isEmpty()) {
                itemDto.setComments(toCommentDtoList(commentList));
            } else {
                itemDto.setComments(Collections.emptyList());
            }
        }

        return resultList;
    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User id " + userId + " not found.");
        }
    }
}
