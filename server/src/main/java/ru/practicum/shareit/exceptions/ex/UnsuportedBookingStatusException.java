package ru.practicum.shareit.exceptions.ex;

public class UnsuportedBookingStatusException extends RuntimeException {
    public UnsuportedBookingStatusException(final String mess) {
        super(mess);
    }
}
