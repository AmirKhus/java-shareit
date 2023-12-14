package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingOwnerDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private long id;

    @NotBlank(message = "В запросе отсутствует имя вещи.")
    private String name;

    @NotBlank(message = "В запросе отсутствует описание вещи.")
    private String description;

    @NotNull(message = "В запросе отсутствует статус запроса к аренде.")
    private Boolean available;

    private BookingOwnerDto lastBooking;

    private BookingOwnerDto nextBooking;

    private List<CommentDto> comments;

    @JsonProperty("requestId")
    private Long itemRequest;
}
