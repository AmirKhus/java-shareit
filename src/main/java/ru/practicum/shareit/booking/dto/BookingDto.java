package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private long id;

    @Min(1)
    private long itemId;

    @NotNull(message = "Отсутствует начальная точка отсчета.")
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull(message = "Отсутствует конечная точка отсчета.")
    @FutureOrPresent
    private LocalDateTime end;

    private UserDto booker;

    private ItemDto item;

    private BookingStatus status;
}
