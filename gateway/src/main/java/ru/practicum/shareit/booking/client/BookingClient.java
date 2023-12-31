package ru.practicum.shareit.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exceptions.ex.CheckStartAndEndBookingException;

import javax.validation.constraints.Min;
import java.util.Map;

@Service
@Validated
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addStatusBooking(long userId, boolean approved, long bookingId) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        String path = String.format("/%d?approved=%b", bookingId, approved);
        return patch(path, userId, parameters);
    }

    public ResponseEntity<Object> getBookings(long userId, BookingStatus state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getBooking(long userId, long bookingId) {
        String path = String.format("/%d", bookingId);
        return get(path, userId);
    }

    public ResponseEntity<Object> createBooking(BookingDto bookingDto, long userId) {

        if (bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new CheckStartAndEndBookingException("Неверны даты начала и окончания аренды.");
        }

        return post("/", userId, null, bookingDto);
    }

    public ResponseEntity<Object> findAllBookingByUserId(long userId,
                                                         BookingStatus state,
                                                         @Min(0) int from,
                                                         @Min(1) int size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findAllBookingByOwnerId(long userId,
                                                          BookingStatus state,
                                                          @Min(0) int from,
                                                          @Min(1) int size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }
}
