package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ex.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Validated
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingStorage;
    private final ItemService itemService;
    private final UserService userService;

    @Transactional
    public BookingDto createBooking(BookingDto bookingDto, long userId) {

        userService.checkExistsUser(userId);

        boolean isAvailableItem = itemService.checkIsAvailableItem(bookingDto.getItemId());

        if (!isAvailableItem) {
            throw new CheckAvailableItemException(
                    String.format("Вещь с id %d недоступна для аренды", bookingDto.getId()));
        }

        Item item = itemService.getItem(bookingDto.getItemId());

        if (item.getOwner() != null && item.getOwner().getId() == userId) {
            throw new CheckUserNotOwnerItemException("Нельзя взять свою вещь в аренду.");
        }

        bookingDto.setBooker(userService.getUserById((userId)));
        bookingDto.setItem(itemService.getItemById(bookingDto.getItemId(), userId));
        bookingDto.setStatus(BookingStatus.WAITING);

        return BookingMapper.toBookingDto(
                bookingStorage.save(BookingMapper.toBooking(bookingDto)));
    }

    @Transactional
    public BookingDto addStatusBooking(long userId, long bookingId, boolean approved) {
        Booking booking = findBookingById(bookingId);

        if (booking.getItem().getOwner().getId() != userId) {
            throw new NoAccessItemException(
                    String.format("Вещь id = %d недоступна для юзера id = %d.", booking.getId(), userId));
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED)
                || booking.getStatus().equals(BookingStatus.REJECTED)) {
            throw new CanNotBeChangedException("Нельзя изменить статус у аренды id = " + booking.getId());
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingStorage.save(booking));
    }

    public BookingDto getBookingById(long userId, long bookingId) {
        userService.checkExistsUser(userId);
        Booking booking = findBookingById(bookingId);

        if (booking.getBooker().getId() == userId
                || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.toBookingDto(bookingStorage.getReferenceById(bookingId));
        }

        throw new NoAccessItemException(
                String.format("Вещь с id = %d недоступна для юзера id = %d.", booking.getItem().getId(), userId));
    }

    public List<BookingDto> findAllBookingByUserId(long userId, String state, int from, int size) {
        userService.checkExistsUser(userId);
        List<Booking> bookings;
        BookingStatus status = konvertBookingStatus(state);
        PageRequest pageRequest = Page.createPageRequest(from, size);
        switch (status) {
            case ALL:
                bookings = bookingStorage.findByBookerIdOrderByStartDesc(userId, pageRequest);
                break;
            case PAST:
                bookings = bookingStorage.findByBookerIdAndEndBeforeOrderByStartDesc(userId,
                        LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByBookerIdAndStartAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), pageRequest);
                break;
            case CURRENT:
                bookings = bookingStorage.getBookingCurrentByUserId(userId,
                        BookingStatus.APPROVED, BookingStatus.WAITING, BookingStatus.REJECTED);
                break;
            default:
                bookings = bookingStorage.findByBookerIdAndStatusOrderByStartDesc(userId, status, pageRequest);
                break;
        }

        return getListBookingDto(bookings);
    }

    public List<BookingDto> findAllBookingByOwnerId(long userId, String state, int from, int size) {
        userService.checkExistsUser(userId);
        PageRequest pageRequest = Page.createPageRequest(from, size);
        List<Booking> bookings;
        BookingStatus status = konvertBookingStatus(state);

        switch (status) {
            case ALL:
                bookings = bookingStorage.getAllBookingByOwnerId(userId, pageRequest);
                break;
            case PAST:
                bookings = bookingStorage.getPastBookingByOwnerId(userId, pageRequest);
                break;
            case FUTURE:
                bookings = bookingStorage.getFutureBookingByOwnerId(userId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingStorage.getBookingCurrentByOwnerId(userId,
                        BookingStatus.APPROVED,
                        BookingStatus.WAITING,
                        BookingStatus.REJECTED,
                        pageRequest);
                break;
            default:
                bookings = bookingStorage.getBookingWithStatusByOwnerId(userId, status, pageRequest);
                break;
        }

        return getListBookingDto(bookings);
    }

    private List<BookingDto> getListBookingDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private Booking findBookingById(long bookingId) {
        Optional<Booking> optionalBooking = bookingStorage.findById(bookingId);

        if (optionalBooking.isEmpty()) {
            throw new BookingNotFoundException(String.format("Аренда с id = %d не найдена.", bookingId));
        }

        return optionalBooking.get();
    }

    private BookingStatus konvertBookingStatus(String state) {
        try {
            return BookingStatus.valueOf(state);
        } catch (RuntimeException e) {
            throw new UnsuportedBookingStatusException("Unknown state: " + state);
        }
    }
}