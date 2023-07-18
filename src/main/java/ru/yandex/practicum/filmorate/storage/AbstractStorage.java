package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractStorage<T extends Entity> implements CommonStorage<T> {

    protected final Map<Long, T> storage = new HashMap<>();
    private long counter = 0L;

    public T create(final T data) {
        log.info("Creating {}: {}", data.getClass().getName().replaceFirst("ru.yandex.practicum.filmorate" +
                ".model.", ""), data);
        validateData(data);
        data.setId(++counter);
        storage.put(data.getId(), data);
        return data;
    }

    public T update(final T data) {
        log.info("Updating {}: {}", data.getClass().getName().replaceFirst("ru.yandex.practicum.filmorate" +
                ".model.", ""), data);
        long id = data.getId();
        if (!storage.containsKey(id)) {
            log.info("Received incorrect id: {}", id);
            throw new NotFoundException("Получено некорректное id - " + id + ".");
        }
        storage.put(id, data);
        return data;
    }

    public List<T> getAll() {
        return new ArrayList<>(storage.values());
    }

    public T getById(long id) {
        if (!storage.containsKey(id)) {
            throw new NotFoundException("Получено некорректное id - " + id + ".");
        }
        return storage.get(id);
    }

    public abstract void validateData(T data);
}
