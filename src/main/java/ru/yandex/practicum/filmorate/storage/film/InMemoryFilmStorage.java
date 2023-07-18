package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
public class InMemoryFilmStorage extends AbstractStorage<Film> implements FilmStorage {

    private final UserStorage userStorage;
    @Autowired
    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void validateData(Film film) {
    }

    public void like(long filmId, long userId) {
        checkFilmId(filmId);
        checkUserId(userId);
        storage.get(filmId).getLikes().add(userId);
        log.info("Like from user with id {} is added", userId);
    }

    public void deleteLike(long filmId, long userId) {
        checkFilmId(filmId);
        checkUserId(userId);
        storage.get(filmId).getLikes().remove(userId);
        log.info("Like from user with id {} is removed", userId);
    }

    public List<Film> getMostPopular(int count) {
        return storage.values().stream()
                .sorted(Comparator.comparing(Film::getLikesQty).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void checkFilmId(long id) {
        if (!storage.containsKey(id)) {
            log.info("Film with id {} is not found", id);
            throw new NotFoundException("Фильм с  id " + id + "не найден.");
        }
    }

    private void checkUserId(long id) {
        if (userStorage.getById(id) == null) {
            log.info("User with id {} is not found", id);
            throw new NotFoundException("Пользователь с  id " + id + "не найден.");
        }
    }
}
