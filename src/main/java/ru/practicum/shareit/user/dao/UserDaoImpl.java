package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundUserException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
public class UserDaoImpl implements UserDao {
    Map<Long, User> users = new HashMap<>();
    private Long count = 0L;


    public User create(User user) {
        if (users.values().stream().map(User::getEmail).anyMatch(s -> s.equals(user.getEmail())))
            throw new DuplicateEmailException("Пользователь с таким email уже существует: " + user.getEmail());
        user.setId(++count);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(Long id, User user) {
        if (user.getName() != null)
            users.get(user.getId()).setName(user.getName());
        if (user.getEmail() != null) {
            if (!users.containsKey(id))
                throw new NotFoundUserException("Пользователя с " + id + " не существует");
            if (users.values().stream().map(User::getEmail).anyMatch(s -> s.equals(user.getEmail())))
                throw new DuplicateEmailException("Пользователь с таким email уже существует: " + user.getEmail());
        } else
            users.get(user.getId()).setEmail(user.getEmail());
        return users.get(user.getId());
    }

    @Override
    public void delete(Long id) {
        checkUser(id);
        users.remove(id);
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    private void checkUser(Long id) {
        if (!users.containsKey(id))
            throw new NotFoundUserException("Пользователя с " + id + " не существует");

    }
}
