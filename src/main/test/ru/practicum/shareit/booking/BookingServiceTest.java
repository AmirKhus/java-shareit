package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.Utils.checkPageSize;

@SpringBootTest
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private BookingRepository bookingRepository;

    private User firstUser;

    private User secondUser;

    private Item item;

    private ItemDto itemDto;

    private Booking firstBooking;

    private Booking secondBooking;

    private BookingDto bookingDto;

    @BeforeEach
    void beforeEach() {
        firstUser = User.builder()
                .id(1L)
                .name("Anna")
                .email("anna@yandex.ru")
                .build();

        secondUser = User.builder()
                .id(2L)
                .name("Tiana")
                .email("tiana@yandex.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("screwdriver")
                .description("works well, does not ask to eat")
                .available(true)
                .user(firstUser)
                .build();

        itemDto = ItemMapper.toEntityItemDto(item);

        firstBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(firstUser)
                .status(Status.APPROVED)
                .build();

        secondBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(firstUser)
                .status(Status.WAITING)
                .build();

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 12, 21, 0, 0))
                .end(LocalDateTime.of(2023, 12, 22, 0, 0))
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void  addBooking() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));
        when(bookingRepository.save(any(Booking.class))).thenReturn(firstBooking);


        BookingOutDto bookingOutDtoTest = bookingService.addBooking(bookingDto, anyLong());

        assertEquals(bookingOutDtoTest.getItem(), itemDto);
        assertEquals(bookingOutDtoTest.getBooker(), UserMapper.toUserEntityDto(secondUser));

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void  addBookingWrongOwner() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingDto, anyLong()));
    }

    @Test
    void  addBookingItemBooked() {

        item.setAvailable(false);

        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));

        assertThrows(ValidationException.class, () -> bookingService.addBooking(bookingDto, anyLong()));
    }

    @Test
    void  addBookingNotValidEnd() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));

        bookingDto.setEnd(LocalDateTime.of(2022, 10, 12, 0, 0));

        assertThrows(ValidationException.class, () -> bookingService.addBooking(bookingDto, anyLong()));
    }

    @Test
    void  addBookingNotValidStart() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));

        bookingDto.setStart(LocalDateTime.of(2023, 10, 12, 0, 0));

        assertThrows(IncorrectDataException.class, () -> bookingService.addBooking(bookingDto, anyLong()));
    }

    @Test
    void  confirmationBooking() {
        BookingOutDto bookingOutDtoTest;

        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(secondBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(secondBooking);

        bookingOutDtoTest = bookingService.confirmationBooking(firstUser.getId(), item.getId(), true);
        assertEquals(bookingOutDtoTest.getStatus(), Status.APPROVED);

        bookingOutDtoTest = bookingService.confirmationBooking(firstUser.getId(), item.getId(), false);
        assertEquals(bookingOutDtoTest.getStatus(), Status.REJECTED);

        verify(bookingRepository, times(2)).save(any(Booking.class));
    }

    @Test
    void  confirmationBookingWrongUser() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(secondBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(secondBooking);

        assertThrows(NotFoundException.class, () -> bookingService.confirmationBooking(secondUser.getId(), item.getId(), true));
    }

    @Test
    void  confirmationBookingWrongStatus() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(firstBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(firstBooking);

        assertThrows(IncorrectDataException.class, () -> bookingService.confirmationBooking(firstUser.getId(), item.getId(), true));
    }

    @Test
    void  getBookingById() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));
        when(bookingRepository.save(any(Booking.class))).thenReturn(firstBooking);


        bookingService.addBooking(bookingDto, firstUser.getId());

        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(firstBooking));
        when(userRepository.existsById(anyLong())).thenReturn(true);


        BookingOutDto bookingOutDtoTest = bookingService.getBookingById(firstUser.getId(), firstBooking.getId());

        assertEquals(bookingOutDtoTest.getItem(), itemDto);
        assertEquals(bookingOutDtoTest.getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.getBooker(), UserMapper.toUserEntityDto(firstUser));

    }

    @Test
    void  getBookingByIdError() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(firstBooking));
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(2L, firstBooking.getId()));
    }

    @Test

    void getAllBookingsByBookerId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any(PageRequest.class))).thenReturn((new PageImpl<>(List.of(firstBooking)).toList()));
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));
        when(bookingRepository.save(any(Booking.class))).thenReturn(firstBooking);


        bookingService.addBooking(bookingDto, firstUser.getId());

        String state = "ALL";

        List<BookingOutDto> bookingOutDtoTest = bookingService.getAllBrookingByBookerId(firstUser.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserEntityDto(firstUser));

        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn((new PageImpl<>(List.of(firstBooking)).toList()));
        state = "CURRENT";

        bookingOutDtoTest = bookingService.getAllBrookingByBookerId(firstUser.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserEntityDto(firstUser));

        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn((new PageImpl<>(List.of(firstBooking)).toList()));
        state = "PAST";

        bookingOutDtoTest = bookingService.getAllBrookingByBookerId(firstUser.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserEntityDto(firstUser));

        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn((new PageImpl<>(List.of(firstBooking)).toList()));
        state = "FUTURE";

        bookingOutDtoTest = bookingService.getAllBrookingByBookerId(firstUser.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserEntityDto(firstUser));

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class), any(PageRequest.class))).thenReturn((new PageImpl<>(List.of(firstBooking)).toList()));
        state = "WAITING";

        bookingOutDtoTest = bookingService.getAllBrookingByBookerId(firstUser.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserEntityDto(firstUser));

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class), any(PageRequest.class))).thenReturn((new PageImpl<>(List.of(firstBooking)).toList()));
        state = "REJECTED";

        bookingOutDtoTest = bookingService.getAllBrookingByBookerId(firstUser.getId(), state, 5, 10);

        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.toUserEntityDto(firstUser));
    }


    @Test
    void getAllBookingsForAllItemsByOwnerIdNotHaveItems() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByUserId(anyLong())).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsForAllItemsByOwnerId(firstUser.getId(), "APPROVED", 5, 10));
    }
}