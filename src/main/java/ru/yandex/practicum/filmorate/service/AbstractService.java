package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.storage.CommonStorage;

import java.util.List;

public abstract class AbstractService<T extends Entity, E extends CommonStorage<T>> implements CommonService<T> {

    protected final E storage;

    //@Autowired
    public AbstractService(E storage) {
        this.storage = storage;
    }

    public T create(T data) {
        return storage.create(data);
    }

    public T update(T data) {
        return storage.update(data);
    }

    public List<T> getAll() {
        return storage.getAll();
    }

    public T getById(long id) {
        return storage.getById(id);
    }

}
