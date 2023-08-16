package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

public class InMemoryMpaStorage extends AbstractStorage<Mpa> implements MpaStorage {
    @Override
    public void validateData(Mpa data) {
    }

}
