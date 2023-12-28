package ru.practicum.shareit.exceptions.ex;

public class CheckUserException extends RuntimeException {
    public CheckUserException(final String mess) {
        super(mess);
    }
}
