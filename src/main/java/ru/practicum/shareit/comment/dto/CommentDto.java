package ru.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.MarkerValidate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    public Long id;
    @NotNull(groups = {MarkerValidate.OnCreate.class})
    private String text;
    private LocalDateTime created;
    private String authorName;
}