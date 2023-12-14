package ru.practicum.shareit.exceptions.ex;

public class ItemRequestNotFoundException extends RuntimeException {
    public ItemRequestNotFoundException(final String mess) {
        super(mess);
    }
}
