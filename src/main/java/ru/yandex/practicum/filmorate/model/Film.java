package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film implements Entity {

    private long id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Length(max = 200, message = "Максимальная длина описания фильма не должна превышать 200 символов")
    private  String description;
    @MinimumDate(message = "Дата релиза не может быть раньше 28.12.1895")
    private  LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private long duration;
    private final Set<Long> likes = new HashSet<>();
    private Set<Genre> genres;
    private Mpa mpa;

    public int getLikesQty() {
        return likes.size();
    }

}

