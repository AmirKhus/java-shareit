package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.MarkerValidate;
import ru.practicum.shareit.booking.entity.Status;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BookingDto {

    private Long itemId;

    @NotNull(groups = MarkerValidate.OnCreate.class)
    private LocalDateTime start;

    @NotNull(groups = MarkerValidate.OnCreate.class)
    private LocalDateTime end;

    private Status status;
}