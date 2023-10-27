package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userRepository;

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("User with id = " + id + " not found"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        validateUniqueEmail(userDto);
        User user = userRepository.create(UserMapper.fromUserDto(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.getById(id).orElseThrow(() ->
                new NotFoundException(String.format("Object class %s not found", User.class)));
        String name = userDto.getName();
        String email = userDto.getEmail();
        if (name != null && !name.isBlank()) {
            user.setName(name);
        }
        if (email != null && !email.isBlank()) {
            if (!user.getEmail().equals(userDto.getEmail())) {
                validateUniqueEmail(userDto);
            }
            user.setEmail(email);
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }

    private void validateUniqueEmail(UserDto userDto) {
        if (userRepository.getAll().stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail()))) {
            throw new ConflictException(String.format("Email %s used.", userDto.getEmail()));
        }
    }
}
