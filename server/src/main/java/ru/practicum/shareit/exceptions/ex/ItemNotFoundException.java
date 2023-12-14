package ru.practicum.shareit.exceptions.ex;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(final String mess) {
        super(mess);
    }
}
