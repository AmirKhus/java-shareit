package ru.practicum.shareit;

public interface BaseService<T> {
    T getById(Long id);

    void delete(Long id);
}
