package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.service.Mpa.Mpa;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class RatingController extends AbstractController<ru.yandex.practicum.filmorate.model.Mpa, Mpa> {

    @Autowired
    public RatingController(Mpa service) {
        super(service);
    }
}
