package ru.practicum.shareit.exception;

public class OneEntityDoesNotBelongToAnotherException extends RuntimeException {
    public OneEntityDoesNotBelongToAnotherException(String message) {
        super(message);
    }
}
