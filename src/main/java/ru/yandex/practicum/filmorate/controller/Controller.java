package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class Controller<T> {

    private Map<Long, T> data = new HashMap<>();
    private long id = 1;

    @PostMapping
    public T create(@RequestBody @Valid T value) {
        log.info("Creating {}: {}", value.getClass().getName().replaceFirst("ru.yandex.practicum.filmorate" +
                ".model.", ""), value);
        long valueId = validateValue(value);
        data.put(valueId, value);
        return value;
    }

    @PutMapping
    public T update(@RequestBody @Valid T value) {
        log.info("Updating {}: {}", value.getClass().getName().replaceFirst("ru.yandex.practicum.filmorate" +
                ".model.", ""), value);
        long valueId = getValueId(value);
        if (!data.containsKey(valueId)) {
            log.info("Received incorrect id: {}", valueId);
            throw new ValidationException("Такого id нет");
        }
        data.put(valueId, value);
        return value;
    }

    @GetMapping
    public List<T> getAll() {
        return new ArrayList<>(data.values());
    }

    private long getValueId(T value) {
        if (value instanceof Film) {
            Film film = (Film) value;
            return film.getId();
        } else {
            User user = (User) value;
            return user.getId();
        }
    }

    private long validateValue(T value) {
        User user = null;
        Film film = null;
        if (value instanceof User) {
            user = (User) value;
            user.setId(id++);
            log.info("User id is set to: {}", user.getId());
            if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
                log.info("No user name is received");
                user.setName(user.getLogin());
                log.info("User name is set to: {}", user.getLogin());
            }
            return user.getId();
        } else {
            film = (Film) value;
            film.setId(id++);
            log.info("Film id is set to: {}", film.getId());
            return film.getId();
        }
    }
}