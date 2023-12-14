package ru.practicum.shareit.exceptions.ex;

public class CheckAvailableItemException extends RuntimeException {
    public CheckAvailableItemException(final String mess) {
        super(mess);
    }
}
