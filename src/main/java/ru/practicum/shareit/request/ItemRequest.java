package ru.practicum.shareit.request;

import ru.practicum.shareit.user.User;

import java.sql.Date;

/**
 * TODO Sprint add-item-requests.
 */
public class ItemRequest {
    private Long id;
    private String description;
    private User requestor;
    private Date created;
}
