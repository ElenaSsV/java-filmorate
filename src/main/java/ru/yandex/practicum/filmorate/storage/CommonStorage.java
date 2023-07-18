package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface CommonStorage<T> {

    T create (T data);

    T update(T data);

    List<T> getAll();

    T getById(long id);

}
