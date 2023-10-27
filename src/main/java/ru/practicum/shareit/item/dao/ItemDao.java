package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.BaseDao;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao extends BaseDao<Item> {
    Item create(Item item);

    List<Item> getByUser(Long userId);

    List<Item> search(String query);
}
