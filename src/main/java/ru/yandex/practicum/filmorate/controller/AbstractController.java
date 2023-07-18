package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.service.CommonService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public abstract class AbstractController<T extends Entity, E extends CommonService<T>> {

    protected final E service;

    @Autowired
    public AbstractController(E service) {
        this.service = service;
    }

    @PostMapping
    public T create(@RequestBody @Valid final T data) {
        return service.create(data);
    }

    @PutMapping
    public T update(@RequestBody @Valid final T data) {
        return service.update(data);
    }

    @GetMapping
    public List<T> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public T getById(@PathVariable("id") Long id) {
        return service.getById(id);
    }

}