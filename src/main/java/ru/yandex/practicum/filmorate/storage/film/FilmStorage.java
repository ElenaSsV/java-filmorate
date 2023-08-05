package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.CommonStorage;

import java.util.List;

public interface FilmStorage extends CommonStorage<Film> {

    void like(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    List<Film> getMostPopular(int count);
}
