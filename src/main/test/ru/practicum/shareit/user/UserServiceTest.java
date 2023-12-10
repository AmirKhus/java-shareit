package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.entity.User;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private User firstUser;

    private User secondUser;

    private UserDto firstUserDto;

    private UserDto secondUserDto;

    @BeforeEach
    void beforeEach() {
        firstUser = User.builder()
                .id(1L)
                .name("Anna")
                .email("anna@yandex.ru")
                .build();

        firstUserDto = UserMapper.toUserEntityDto(firstUser);

        secondUser = User.builder()
                .id(2L)
                .name("Tiana")
                .email("tiana@yandex.ru")
                .build();

        secondUserDto = UserMapper.toUserEntityDto(secondUser);
    }

    @Test
    void addUser() {
        when(userRepository.save(any(User.class))).thenReturn(firstUser);

        UserDto userDtoTest = userService.create(firstUserDto);

        assertEquals(userDtoTest.getId(), firstUserDto.getId());
        assertEquals(userDtoTest.getName(), firstUserDto.getName());
        assertEquals(userDtoTest.getEmail(), firstUserDto.getEmail());

        verify(userRepository, times(1)).save(firstUser);
    }

    @Test
    void updateUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(userRepository.findByEmail(anyString())).thenReturn(List.of(firstUser));
        when(userRepository.save(any(User.class))).thenReturn(firstUser);

        firstUserDto.setName("Sofia");
        firstUserDto.setEmail("Sofia@yandex.ru");

        UserDto userDtoUpdated = userService.update(1L,firstUserDto);

        assertEquals(userDtoUpdated.getName(), firstUserDto.getName());
        assertEquals(userDtoUpdated.getEmail(), firstUserDto.getEmail());

        verify(userRepository, times(1)).findByEmail(firstUser.getEmail());
        verify(userRepository, times(1)).save(firstUser);
    }

    @Test
    void updateUser_wrongEmail() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(userRepository.findByEmail(anyString())).thenReturn(List.of(firstUser));

        firstUserDto.setEmail("");
    }


    @Test
    void getUserById() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));

        UserDto userDtoTest = userService.getById(1L);

        assertEquals(userDtoTest.getId(), firstUserDto.getId());
        assertEquals(userDtoTest.getName(), firstUserDto.getName());
        assertEquals(userDtoTest.getEmail(), firstUserDto.getEmail());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(firstUser, secondUser));

        List<UserDto> userDtoList = userService.getAll();

        assertEquals(userDtoList, List.of(firstUserDto, secondUserDto));

        verify(userRepository, times(1)).findAll();
    }
}