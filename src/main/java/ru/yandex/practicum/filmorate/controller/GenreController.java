package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController extends AbstractController<Genre, GenreService> {

    @Autowired
    public GenreController(GenreService service) {
        super(service);
    }
}
