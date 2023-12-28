package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ex.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;

    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(repository.save(user));
    }

    public UserDto getUserById(long userId) {
        checkExistsUser(userId);
        return UserMapper.toUserDto(repository.getReferenceById(userId));
    }

    public List<UserDto> getAllUsers() {
        return repository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUser(UserDto userDto, long userId) {
        checkExistsUser(userId);
        User updateUser = repository.getReferenceById(userId);

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            updateUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            updateUser.setName(userDto.getName());
        }

        return UserMapper.toUserDto(repository.save(updateUser));
    }

    public void deleteUser(long id) {
        repository.deleteById(id);
    }

    public void checkExistsUser(long userId) {
        log.info("Проверка юзера с id = " + userId);
        if (repository.findById(userId).isEmpty()) {
            log.info("Не нашли юзера с id = " + userId);
            throw new NotFoundException(String.format("Пользователь с id = %d не существует", userId));
        }
    }
}
