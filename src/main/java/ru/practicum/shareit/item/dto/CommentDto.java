package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private long id;

    @NotBlank(message = "В запросе отсутствует текст отзыва.")
    @Length(max = 2000, message = "Превышена длина отзыва.")
    private String text;

    private String authorName;

    private LocalDateTime created;
}
