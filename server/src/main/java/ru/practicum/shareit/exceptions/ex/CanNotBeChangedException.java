package ru.practicum.shareit.exceptions.ex;

public class CanNotBeChangedException extends RuntimeException {
    public CanNotBeChangedException(final String mess) {
        super(mess);
    }
}
