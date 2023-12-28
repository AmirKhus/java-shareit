package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingOwnerDto;

import java.util.List;

@Data
@Builder
public class ItemOwnerDto {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingOwnerDto lastBooking;

    private BookingOwnerDto nextBooking;

    private List<CommentDto> comments;
}
