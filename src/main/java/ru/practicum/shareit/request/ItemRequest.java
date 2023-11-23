package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.sql.Date;

@Data
@Builder
public class ItemRequest {
    private Long id;
    private String description;
    private User requestor;
    private Date created;
}
