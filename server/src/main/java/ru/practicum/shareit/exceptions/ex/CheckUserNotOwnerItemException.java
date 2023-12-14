package ru.practicum.shareit.exceptions.ex;

public class CheckUserNotOwnerItemException extends RuntimeException {
    public CheckUserNotOwnerItemException(final String mess) {
        super(mess);
    }
}
