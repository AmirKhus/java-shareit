package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemDaoImpl implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private Long count = 0L;

    @Override
    public Item create(Item item) {
        count++;
        item.setId(count);
        items.put(count, item);
        return item;
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList(items.values());
    }

    @Override
    public Item update(Long id, Item oldItem) {
        Item newItem = items.get(id);
        items.put(id, newItem);
        return newItem;
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }

    @Override
    public Optional<Item> getById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getByUser(Long userId) {
        return items.values().stream()
                .filter(item -> item.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String query) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(query.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}
