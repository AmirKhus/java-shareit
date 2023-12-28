package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ex.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userStorage;

    @Test
    public void createUserTest() {
        User user = User.builder()
                .name("Дмитрий")
                .build();
        when(userStorage.save(user)).thenReturn(user);

        UserDto saveUser = userService.createUser(UserMapper.toUserDto(user));

        assertEquals("check", user.getName(), saveUser.getName());
    }

    @Test
    public void getUserById_whenUserFound_thenReturnUser_test() {
        long userId = 0;
        UserDto userExpected = UserDto.builder().build();
        when(userStorage.findById(userId)).thenReturn(Optional.of(new User()));
        when(userStorage.getReferenceById(userId)).thenReturn(UserMapper.toUser(userExpected));

        UserDto actualUser = userService.getUserById(userId);

        assertEquals("check", userExpected, actualUser);
    }

    @Test
    public void getUserById_whenUserNotFound_thenReturnException_test() {
        long userId = 99;
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    public void getAllUsers_whenInvokeFindAll_test() {
        List<User> expectedUsers = List.of(new User());
        when(userStorage.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.getAllUsers().stream()
                .map(UserMapper::toUser)
                .collect(Collectors.toList());

        assertEquals("check", expectedUsers, actualUsers);
    }

    @Test
    public void deleteUserTest() {
        long userId = 1;
        userService.deleteUser(1);
        verify(userStorage, times(1)).deleteById(userId);
    }

    @Test
    public void updateUserTest() {
        long userId = 1;
        User userExpected = User.builder()
                .id(userId)
                .name("Дмитрий")
                .email("mail@mail.ru")
                .build();
        when(userStorage.findById(userId)).thenReturn(Optional.of(new User()));
        when(userStorage.getReferenceById(userId)).thenReturn(User.builder()
                                                                  .name("Владимир")
                                                                  .id(1)
                                                                  .build());
        when(userStorage.save(userExpected)).thenReturn(userExpected);

        User actualUserDto = UserMapper.toUser(userService.updateUser(UserMapper.toUserDto(userExpected), userId));

        assertEquals("check", userExpected, actualUserDto);
    }
}