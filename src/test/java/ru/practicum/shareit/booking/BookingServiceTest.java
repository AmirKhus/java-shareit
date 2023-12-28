package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.ex.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingStorage;

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @Test
    public void createBooking_whenNotValidDateTest() {
        long userId = 1;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 12, 3, 12, 0))
                .end(LocalDateTime.of(2023, 12, 2, 12, 0))
                .build();

        assertThrows(CheckStartAndEndBookingException.class,
                () -> bookingService.createBooking(bookingDto, userId));
    }

    @Test
    public void createBooking_whenNotAvailableItemTest() {
        long userId = 1;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 12, 2, 12, 0))
                .end(LocalDateTime.of(2023, 12, 3, 12, 0))
                .build();

        when(itemService.checkIsAvailableItem(bookingDto.getItemId())).thenReturn(false);

        assertThrows(CheckAvailableItemException.class,
                () -> bookingService.createBooking(bookingDto, userId));
    }

    @Test
    public void createBooking_whenUserOwnerItemTest() {
        long userId = 1;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 12, 2, 12, 0))
                .end(LocalDateTime.of(2023, 12, 3, 12, 0))
                .build();

        Item item = Item.builder()
                        .owner(User.builder()
                                   .id(1)
                                   .build())
                        .build();

        when(itemService.checkIsAvailableItem(bookingDto.getItemId())).thenReturn(true);
        when(itemService.getItem(bookingDto.getItemId())).thenReturn(item);

        assertThrows(CheckUserNotOwnerItemException.class,
                () -> bookingService.createBooking(bookingDto, userId));
    }

    @Test
    public void createBookingTest() {
        long userId = 2;
        long itemId = 1;
        UserDto booker = UserDto.builder()
                .id(userId)
                .build();
        Item item = Item.builder()
                .id(itemId)
                .owner(User.builder()
                        .id(1)
                        .build())
                .build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(0)
                .itemId(itemId)
                .start(LocalDateTime.of(2023, 12, 2, 12, 0))
                .end(LocalDateTime.of(2023, 12, 3, 12, 0))
                .booker(booker)
                .item(ItemMapper.toItemDto(item))
                .status(BookingStatus.WAITING)
                .build();

        when(itemService.checkIsAvailableItem(expectedBookingDto.getItemId())).thenReturn(true);
        when(itemService.getItem(expectedBookingDto.getItemId())).thenReturn(item);
        when(userService.getUserById(userId)).thenReturn(booker);
        when(itemService.getItemById(expectedBookingDto.getItemId(), userId))
                .thenReturn(ItemMapper.toItemDto(item));
        when(bookingStorage.save(BookingMapper.toBooking(expectedBookingDto)))
                .thenReturn(BookingMapper.toBooking(expectedBookingDto));

        BookingDto actualBookingDto = bookingService.createBooking(expectedBookingDto, userId);

        assertEquals("check", expectedBookingDto, actualBookingDto);
    }

    @Test
    public void addStatusBooking_whenNotFoundBookingTest() {
        long userId = 1;
        long bookingId = 1;
        boolean isApproved = false;

        User user = User.builder()
                .id(2)
                .build();

        Item item = Item.builder()
                .owner(user)
                .build();

        Booking booking = Booking.builder()
                .item(item)
                .build();

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.addStatusBooking(userId, bookingId, isApproved));
    }

    @Test
    public void addStatusBooking_whenUserNotOwnerItemTest() {
        long userId = 1;
        long bookingId = 1;
        boolean isApproved = false;

        User user = User.builder()
                .id(2)
                .build();

        Item item = Item.builder()
                .owner(user)
                .build();

        Booking booking = Booking.builder()
                .item(item)
                .build();

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));
        assertThrows(NoAccessItemException.class,
                () -> bookingService.addStatusBooking(userId, bookingId, isApproved));
    }

    @Test
    public void addStatusBooking_whenNotValidBookingStatus() {
        long userId = 1;
        long bookingId = 1;
        boolean isApproved = false;

        User user = User.builder()
                .id(userId)
                .build();

        Item item = Item.builder()
                .owner(user)
                .build();

        Booking booking = Booking.builder()
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));
        assertThrows(CanNotBeChangedException.class,
                () -> bookingService.addStatusBooking(userId, bookingId, isApproved));
    }

    @Test
    public void addStatusBooking_whenBookingStatusIsApproved() {
        long userId = 1;
        long bookingId = 1;
        boolean isApproved = true;

        User user = User.builder()
                .id(userId)
                .build();

        Item item = Item.builder()
                .owner(user)
                .build();

        Booking booking = Booking.builder()
                .booker(User.builder().id(1).build())
                .item(item)
                .status(BookingStatus.WAITING)
                .build();

        Booking expectedBooking = Booking.builder()
                .booker(User.builder().id(1).build())
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingStorage.save(expectedBooking)).thenReturn(expectedBooking);

        BookingDto actualBooking = bookingService.addStatusBooking(userId, bookingId, isApproved);
        assertEquals("check", BookingMapper.toBookingDto(expectedBooking), actualBooking);
    }

    @Test
    public void addStatusBooking_whenBookingStatusIsNotApproved() {
        long userId = 1;
        long bookingId = 1;
        boolean isApproved = false;

        User user = User.builder()
                .id(userId)
                .build();

        Item item = Item.builder()
                .owner(user)
                .build();

        Booking booking = Booking.builder()
                .booker(User.builder().id(1).build())
                .item(item)
                .status(BookingStatus.WAITING)
                .build();

        Booking expectedBooking = Booking.builder()
                .booker(User.builder().id(1).build())
                .item(item)
                .status(BookingStatus.REJECTED)
                .build();

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingStorage.save(expectedBooking)).thenReturn(expectedBooking);

        BookingDto actualBooking = bookingService.addStatusBooking(userId, bookingId, isApproved);
        assertEquals("check", BookingMapper.toBookingDto(expectedBooking), actualBooking);
    }

    @Test
    public void getBookingById_whenUserNotBookerOrNotOwnerTest() {
        long userId = 2;
        long bookingId = 1;

        User user = User.builder()
                .id(1)
                .build();

        Item item = Item.builder()
                .owner(User.builder()
                        .id(3)
                        .build())
                .build();

        Booking booking = Booking.builder()
                .booker(user)
                .item(item)
                .build();

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NoAccessItemException.class, () -> bookingService.getBookingById(userId, bookingId));
    }

    @Test
    public void getBookingById_whenUserBookerTest() {
        long userId = 2;
        long bookingId = 1;

        User user = User.builder()
                .id(1)
                .build();

        Item item = Item.builder()
                .owner(User.builder()
                        .id(userId)
                        .build())
                .build();

        Booking booking = Booking.builder()
                .booker(user)
                .item(item)
                .build();

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingStorage.getReferenceById(bookingId)).thenReturn(booking);

        BookingDto actualBooking = bookingService.getBookingById(userId, bookingId);

        assertEquals("Check", BookingMapper.toBookingDto(booking), actualBooking);
    }

    @Test
    public void findAllBookingByUserId_whenNotValidStatusTest() {
        long userId = 1;
        int from = 1;
        int size = 1;
        String state = "ALL1";

        assertThrows(UnsuportedBookingStatusException.class,
                () -> bookingService.findAllBookingByUserId(userId, state, from, size));
    }

    @Test
    public void findAllBookingByUserId_whenStatusAllTest() {
        long userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "ALL";

        when(bookingStorage.findByBookerIdOrderByStartDesc(userId, PageRequest.of(page, size)))
                .thenReturn(Collections.emptyList());

        List<BookingDto> actualBookings = bookingService.findAllBookingByUserId(userId, state, from, size);

        assertEquals("check", 0, actualBookings.size());
        verify(bookingStorage, never())
                .findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), PageRequest.of(page, size));
        verify(bookingStorage, never())
                .findAllByBookerIdAndStartAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingCurrentByUserId(userId,
                BookingStatus.APPROVED, BookingStatus.WAITING, BookingStatus.REJECTED);
        verify(bookingStorage, never()).findByBookerIdAndStatusOrderByStartDesc(userId,
                BookingStatus.WAITING, PageRequest.of(page, size));
    }

    @Test
    public void findAllBookingByUserId_whenStatusPastTest() {
        long userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "PAST";

        when(bookingStorage.findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(PageRequest.class))).thenReturn(Collections.emptyList());

        List<BookingDto> actualBookings = bookingService.findAllBookingByUserId(userId, state, from, size);

        assertEquals("check", 0, actualBookings.size());
        verify(bookingStorage, never())
                .findByBookerIdOrderByStartDesc(userId, PageRequest.of(page, size));
        verify(bookingStorage, never())
                .findAllByBookerIdAndStartAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingCurrentByUserId(userId,
                BookingStatus.APPROVED, BookingStatus.WAITING, BookingStatus.REJECTED);
        verify(bookingStorage, never()).findByBookerIdAndStatusOrderByStartDesc(userId,
                BookingStatus.WAITING, PageRequest.of(page, size));
    }

    @Test
    public void findAllBookingByUserId_whenStatusCurrentTest() {
        long userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "CURRENT";

        when(bookingStorage.getBookingCurrentByUserId(userId,
                BookingStatus.APPROVED,
                BookingStatus.WAITING,
                BookingStatus.REJECTED))
                .thenReturn(Collections.emptyList());

        List<BookingDto> actualBookings = bookingService.findAllBookingByUserId(userId, state, from, size);

        assertEquals("check", 0, actualBookings.size());
        verify(bookingStorage, never())
                .findByBookerIdOrderByStartDesc(userId, PageRequest.of(page, size));
        verify(bookingStorage, never())
                .findAllByBookerIdAndStartAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), PageRequest.of(page, size));
        verify(bookingStorage, never()).findByBookerIdAndStatusOrderByStartDesc(userId,
                BookingStatus.WAITING, PageRequest.of(page, size));
    }

    @Test
    public void findAllBookingByUserId_whenStatusFutureTest() {
        long userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "FUTURE";

        when(bookingStorage.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        List<BookingDto> actualBookings = bookingService.findAllBookingByUserId(userId, state, from, size);

        assertEquals("check", 0, actualBookings.size());
        verify(bookingStorage, never())
                .findByBookerIdOrderByStartDesc(userId, PageRequest.of(page, size));
        verify(bookingStorage, never())
                .findByBookerIdAndEndBeforeOrderByStartDesc(userId,
                        LocalDateTime.now(), PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingCurrentByUserId(userId,
                BookingStatus.APPROVED, BookingStatus.WAITING, BookingStatus.REJECTED);
        verify(bookingStorage, never()).findByBookerIdAndStatusOrderByStartDesc(userId,
                BookingStatus.WAITING, PageRequest.of(page, size));
    }

    @Test
    public void findAllBookingByUserId_whenStatusDefaultTest() {
        long userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "WAITING";

        when(bookingStorage.findByBookerIdAndStatusOrderByStartDesc(anyLong(),
                        any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        List<BookingDto> actualBookings = bookingService.findAllBookingByUserId(userId, state, from, size);

        assertEquals("check", 0, actualBookings.size());
        verify(bookingStorage, never())
                .findByBookerIdOrderByStartDesc(userId, PageRequest.of(page, size));
        verify(bookingStorage, never())
                .findByBookerIdAndEndBeforeOrderByStartDesc(userId,
                        LocalDateTime.now(), PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingCurrentByUserId(userId,
                BookingStatus.APPROVED, BookingStatus.WAITING, BookingStatus.REJECTED);
        verify(bookingStorage, never())
                .findAllByBookerIdAndStartAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), PageRequest.of(page, size));
    }

    @Test
    public void findAllBookingByOwnerId_whenStatusAllTest() {
        long userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "ALL";

        when(bookingStorage.getAllBookingByOwnerId(userId, PageRequest.of(page, size)))
                .thenReturn(Collections.emptyList());

        List<BookingDto> bookings = bookingService.findAllBookingByOwnerId(userId, state, from, size);

        assertEquals("check", 0, bookings.size());

        verify(bookingStorage, never()).getPastBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getFutureBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingCurrentByOwnerId(userId,
                BookingStatus.APPROVED,
                BookingStatus.WAITING,
                BookingStatus.REJECTED,
                PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingWithStatusByOwnerId(anyLong(),
                any(BookingStatus.class), any(PageRequest.class));
    }

    @Test
    public void findAllBookingByOwnerId_whenStatusPastTest() {
        long userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "PAST";

        when(bookingStorage.getPastBookingByOwnerId(userId, PageRequest.of(page, size)))
                .thenReturn(Collections.emptyList());

        List<BookingDto> bookings = bookingService.findAllBookingByOwnerId(userId, state, from, size);

        assertEquals("check", 0, bookings.size());

        verify(bookingStorage, never()).getAllBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getFutureBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingCurrentByOwnerId(userId,
                BookingStatus.APPROVED,
                BookingStatus.WAITING,
                BookingStatus.REJECTED,
                PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingWithStatusByOwnerId(anyLong(),
                any(BookingStatus.class), any(PageRequest.class));
    }

    @Test
    public void findAllBookingByOwnerId_whenStatusFutureTest() {
        long userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "FUTURE";

        when(bookingStorage.getFutureBookingByOwnerId(userId, PageRequest.of(page, size)))
                .thenReturn(Collections.emptyList());

        List<BookingDto> bookings = bookingService.findAllBookingByOwnerId(userId, state, from, size);

        assertEquals("check", 0, bookings.size());

        verify(bookingStorage, never()).getAllBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getPastBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingCurrentByOwnerId(userId,
                BookingStatus.APPROVED,
                BookingStatus.WAITING,
                BookingStatus.REJECTED,
                PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingWithStatusByOwnerId(anyLong(),
                any(BookingStatus.class), any(PageRequest.class));
    }

    @Test
    public void findAllBookingByOwnerId_whenStatusCurrentTest() {
        long userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "CURRENT";

        when(bookingStorage.getBookingCurrentByOwnerId(userId,
                BookingStatus.APPROVED,
                BookingStatus.WAITING,
                BookingStatus.REJECTED,
                PageRequest.of(page, size))).thenReturn(Collections.emptyList());

        List<BookingDto> bookings = bookingService.findAllBookingByOwnerId(userId, state, from, size);

        assertEquals("check", 0, bookings.size());

        verify(bookingStorage, never()).getAllBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getPastBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getFutureBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingWithStatusByOwnerId(anyLong(),
                any(BookingStatus.class), any(PageRequest.class));
    }

    @Test
    public void findAllBookingByOwnerId_whenStatusDefautTest() {
        long userId = 1;
        int from = 1;
        int size = 1;
        int page = from / size;
        String state = "WAITING";

        when(bookingStorage.getBookingWithStatusByOwnerId(anyLong(),
                any(BookingStatus.class), any(PageRequest.class))).thenReturn(Collections.emptyList());

        List<BookingDto> bookings = bookingService.findAllBookingByOwnerId(userId, state, from, size);

        assertEquals("check", 0, bookings.size());

        verify(bookingStorage, never()).getAllBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getPastBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getFutureBookingByOwnerId(userId, PageRequest.of(page, size));
        verify(bookingStorage, never()).getBookingCurrentByOwnerId(userId,
                BookingStatus.APPROVED,
                BookingStatus.WAITING,
                BookingStatus.REJECTED,
                PageRequest.of(page, size));
    }
}