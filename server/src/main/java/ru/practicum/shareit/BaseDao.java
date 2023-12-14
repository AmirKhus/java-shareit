package ru.practicum.shareit;

import java.util.List;
import java.util.Optional;

public interface BaseDao<T> {

    List<T> getAll();

    T update(Long id, T t);

    void delete(Long id);

    Optional<T> getById(Long id);
}
