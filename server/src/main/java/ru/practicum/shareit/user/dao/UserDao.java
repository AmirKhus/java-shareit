package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.BaseDao;
import ru.practicum.shareit.user.User;

public interface UserDao extends BaseDao<User> {
    User create(User user);
}
