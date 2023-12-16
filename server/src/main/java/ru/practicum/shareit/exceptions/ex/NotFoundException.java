package ru.practicum.shareit.exceptions.ex;


public class NotFoundException extends RuntimeException {
    public NotFoundException(final String mess) {
        super(mess);
    }
}
