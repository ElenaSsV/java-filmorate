package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public abstract class Controller<T> {

    protected final Map<Long, T> storage = new HashMap<>();

    @PostMapping
    public T create(@RequestBody @Valid final T data) {
        log.info("Creating {}: {}", data.getClass().getName().replaceFirst("ru.yandex.practicum.filmorate" +
                ".model.", ""), data);
        long id = validateData(data);
        storage.put(id, data);
        return data;
    }

    @PutMapping
    public T update(@RequestBody @Valid final T data) {
        log.info("Updating {}: {}", data.getClass().getName().replaceFirst("ru.yandex.practicum.filmorate" +
                ".model.", ""), data);
        long id = getId(data);
        if (!storage.containsKey(id)) {
            log.info("Received incorrect id: {}", id);
            throw new ValidationException("Такого id нет");
        }
        storage.put(id, data);
        return data;
    }

    @GetMapping
    public List<T> getAll() {
        return new ArrayList<>(storage.values());
    }

    public abstract long getId(T data);

    public abstract long validateData(T data);
}