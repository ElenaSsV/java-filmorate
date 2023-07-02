package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private int id = 1;

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        validate(film);
        film.setId(id++);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильм с таким  id  отсутствует");
        }
        validate(film);
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    private void validate(Film film) {
        int maxDescriptionLength = 200;
        LocalDate earliestReleaseDate = LocalDate.of(1895, 12, 28);

        if (film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        } else if (film.getDescription().length() > maxDescriptionLength) {
            throw new ValidationException("Максимальная длина описания фильма - " + maxDescriptionLength);
        } else if (film.getReleaseDate().isBefore(earliestReleaseDate)) {
            throw new ValidationException("Дата релиза не может быть раньше " +   earliestReleaseDate + ".");
        } else if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
