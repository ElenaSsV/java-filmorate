package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validation.MinimumDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
@Builder
public class Film extends Entity {

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Length(max = 200, message = "Максимальная длина описания фильма не должна превышать 200 символов")
    private String description;
    @MinimumDate(message = "Дата релиза не может быть раньше 28.12.1895")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private long duration;

}

