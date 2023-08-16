package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

public class InMemoryGenreStorage extends AbstractStorage<Genre> implements GenreStorage {

    @Override
    public void validateData(Genre data) {
    }

}
