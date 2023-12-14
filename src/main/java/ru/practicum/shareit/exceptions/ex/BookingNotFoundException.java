package ru.practicum.shareit.exceptions.ex;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(final String mess) {
        super(mess);
    }
}
