package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Entity;

import java.util.List;

public interface CommonService<T extends Entity> {

    T create(T data);

    T update(T data);

    List<T> getAll();

    T getById(long id);
}
