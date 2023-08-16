package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.AbstractService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Slf4j
@Service
public class FilmService extends AbstractService<Film, FilmStorage> {

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        super(filmStorage);
    }

    public void like(long filmId, long userId) {
        storage.like(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        storage.deleteLike(filmId, userId);
    }

    public List<Film> getMostPopular(int count) {
        return storage.getMostPopular(count);
    }
}
