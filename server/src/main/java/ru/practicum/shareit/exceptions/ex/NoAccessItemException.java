package ru.practicum.shareit.exceptions.ex;

public class NoAccessItemException extends RuntimeException {
    public NoAccessItemException(final String mess) {
        super(mess);
    }
}
