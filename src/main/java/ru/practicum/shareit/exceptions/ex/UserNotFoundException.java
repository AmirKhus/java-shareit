package ru.practicum.shareit.exceptions.ex;


public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(final String mess) {
        super(mess);
    }
}
