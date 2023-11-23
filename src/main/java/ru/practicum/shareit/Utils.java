package ru.practicum.shareit;

import org.springframework.data.domain.PageRequest;

import javax.validation.ValidationException;

public class Utils {
    public static final String HEADER_USER_ID = "X-Sharer-User-Id";

    public static PageRequest checkPageSize(Integer from, Integer size) {

        if (from == 0 && size == 0) {
            throw new ValidationException("\"size\" and \"from\"must be not equal 0");
        }

        if (size <= 0) {
            throw new ValidationException("\"size\" must be greater than 0");
        }

        if (from < 0) {
            throw new ValidationException("\"from\" must be greater than or equal to 0");
        }
        return PageRequest.of(from / size, size);
    }
}
