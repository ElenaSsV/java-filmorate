package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends Controller<Film> {
    private long id = 1L;

    public long validateData(Film film) {
        film.setId(id++);
        log.info("Film id is set to: {}", film.getId());
        return film.getId();
    }

    public long getId(Film film) {
        return film.getId();
    }

}