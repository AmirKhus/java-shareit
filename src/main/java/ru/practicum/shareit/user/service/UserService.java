package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.BaseService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService extends BaseService<UserDto> {
    List<UserDto> getAll();

    UserDto create(UserDto userDto);

    UserDto update(Long id, UserDto userDto);
}
