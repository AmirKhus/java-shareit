package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserDto toUserEntityDto(ru.practicum.shareit.user.entity.User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User fromUserDto(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static ru.practicum.shareit.user.entity.User fromEntityUserDto(UserDto userDto) {
        return ru.practicum.shareit.user.entity.User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static List<UserDto> toUserDtoList(Iterable<ru.practicum.shareit.user.entity.User> users) {
        List<UserDto> result = new ArrayList<>();

        for (ru.practicum.shareit.user.entity.User user : users) {
            result.add(toUserEntityDto(user));
        }
        return result;
    }
}
