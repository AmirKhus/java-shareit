package ru.practicum.shareit;

public interface BaseService<T> {
    T getById(Long id);

    T delete(Long id);
}
