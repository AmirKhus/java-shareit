package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.MarkerValidate;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.NotNull;
import java.util.List;


@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping()
    public BookingOutDto addBookings(@RequestHeader(HEADER_USER_ID) Long userId,
                                     @Validated(MarkerValidate.OnCreate.class) @NotNull @RequestBody BookingDto bookingDto) {
        log.info("User {}, add new booking {}", userId, "bookingDto.getName()");
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto confirmationBooking(@RequestHeader(HEADER_USER_ID) Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam(value = "approved") Boolean approved) {
        log.info("User {}, changed the status booking {}", userId, bookingId);
        return bookingService.confirmationBooking(userId, bookingId, approved);
    }


    @GetMapping("/{bookingId}")
    public BookingOutDto getBookingById(@RequestHeader(HEADER_USER_ID) Long userId,
                                        @PathVariable Long bookingId) {

        log.info("Get booking {}", bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }


    @GetMapping
    public List<BookingOutDto> getAllBrookingByBookerId(@RequestHeader(HEADER_USER_ID) Long userId,
                                                        @RequestParam(value = "state", defaultValue = "ALL") String state) {
        log.info("Get all bookings by booker Id {}", userId);
        return bookingService.getAllBrookingByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getAllBookingsForAllItemsByOwnerId(@RequestHeader(HEADER_USER_ID) Long userId,
                                                                  @RequestParam(defaultValue = "ALL", required = false) String state) {

        log.info("Get all bookings for all items by owner Id {}", userId);
        return bookingService.getAllBookingsForAllItemsByOwnerId(userId, state);
    }

}
