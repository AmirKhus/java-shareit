package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Utils;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingDto;
import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingDtoList;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Transactional
    @Override
    public BookingOutDto addBooking(BookingDto bookingDto, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User id " + userId + " not found.");
        }
        User user = getUserById(userId);
        Item item = getItemById(bookingDto.getItemId());

        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);

        if (item.getUser().equals(user)) {
            throw new NotFoundException("Owner " + userId + " can't book his item");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Item " + item.getId() + " is booked");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Start cannot be later than end");
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new IncorrectDataException("Booking: Dates are null!");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().isEqual(bookingDto.getEnd())
                || bookingDto.getEnd().isBefore(LocalDateTime.now()) || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new IncorrectDataException("Booking: Problem in dates");
        }
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        return toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingOutDto confirmationBooking(Long userId, Long bookingId, Boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("There is no Booking with Id: " + bookingId));

        if (!Objects.equals(booking.getItem().getUser().getId(), userId)) {
            throw new NotFoundException("User with id = " + userId + " is not an owner!");
        }

        if (approved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new IncorrectDataException("Status is Approved");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return toBookingDto(booking);
    }

    @Transactional
    @Override
    public List<BookingOutDto> getAllBrookingByBookerId(Long userId, String state, Integer from, Integer size) {
        List<Booking> bookings = null;

        PageRequest pageRequest = Utils.checkPageSize(from, size);

        getUserById(userId);
        LocalDateTime localDate = LocalDateTime.now();
        State stateEnum;
        try {
            stateEnum = State.getEnumValue(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }

        switch (stateEnum) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId,pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, localDate, localDate,pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, localDate,pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, localDate,pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING,pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED,pageRequest);
                break;
            default:
                throw new NotFoundException("Booking status not found");
        }
        return toBookingDtoList(bookings);
    }

    @Transactional
    @Override
    public List<BookingOutDto> getAllBookingsForAllItemsByOwnerId(Long userId, String state, Integer from, Integer size) {
        getUserById(userId);

        PageRequest pageRequest = Utils.checkPageSize(from, size);
        
        if (itemRepository.findByUserId(userId).isEmpty()) {
            throw new ValidationException("User does not have items to booking");
        }
        List<Booking> bookings = null;
        LocalDateTime localDate = LocalDateTime.now();
        State stateEnum;
        try {
            stateEnum = State.getEnumValue(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }

        switch (stateEnum) {
            case ALL:
                bookings = bookingRepository.findAllByItemUserIdOrderByStartDesc(userId,pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findAllByItemUserIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, localDate, localDate,pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemUserIdAndEndBeforeOrderByStartDesc(userId, localDate,pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemUserIdAndStartAfterOrderByStartDesc(userId, localDate,pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemUserIdAndStatusOrderByStartDesc(userId, Status.WAITING,pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemUserIdAndStatusOrderByStartDesc(userId, Status.REJECTED,pageRequest);
                break;
            default:
                throw new NotFoundException("Booking status not found");
        }
        return toBookingDtoList(bookings);
    }

    @Transactional
    @Override
    public BookingOutDto getBookingById(Long userId, Long bookingId) {
        getUserById(userId);
        Booking booking = getBookingById(bookingId);

        if (booking.getBooker().getId().equals(userId) || booking.getItem().getUser().getId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("To get information about the reservation, the item of the reservation or the owner {} " + userId + "of the item can");
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("There is no user with id: " + userId));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("There is no item with id: " + itemId));
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("There is no Booking with id: " + bookingId));
    }
}
