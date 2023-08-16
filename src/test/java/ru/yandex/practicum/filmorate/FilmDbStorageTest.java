package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    public Film getTestFilm() {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Test2 desc");
        film.setReleaseDate(LocalDate.of(1967, 3, 25));
        film.setDuration(100);
        film.setGenres(new HashSet<>(Collections.singletonList(new Genre(1, "Комедия"))));
        film.setMpa(new Mpa(2, "PG"));

        return film;
    }

    public Film getTestFilm2() {
        Film film2 = new Film();
        film2.setName("Test2");
        film2.setDescription("Test2 desc");
        film2.setReleaseDate(LocalDate.of(1967, 3, 25));
        film2.setDuration(100);
        film2.setGenres(new HashSet<>(Collections.singletonList(new Genre(2, "Драма"))));
        film2.setMpa(new Mpa(2, "PG"));

        return film2;
    }

    public User getTestUser() {
        User user = new User();
        user.setLogin("testLogin");
        user.setName("Test Name");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.of(1990, 8, 20));

        return user;
    }

    public User getTestUser2() {
        User user2 = new User();
        user2.setLogin("test2Login");
        user2.setName("Test2 Name");
        user2.setEmail("mail2@mail.ru");
        user2.setBirthday(LocalDate.of(1990, 8, 20));

        return user2;
    }

    @Test
    public void testUpdateFilm() {
        filmStorage.create(getTestFilm());

        Film updatedFilm = new Film();
        updatedFilm.setId(1);
        updatedFilm.setName("Testupdated");
        updatedFilm.setDescription("Test2 desc");
        updatedFilm.setReleaseDate(LocalDate.of(1967, 3, 25));
        updatedFilm.setDuration(90);
        updatedFilm.setGenres(new HashSet<>(Collections.singletonList(new Genre(2, "Драма"))));
        updatedFilm.setMpa(new Mpa(2, "PG"));

        Film updated = filmStorage.update(updatedFilm);
        assertEquals(updatedFilm, updated);
    }

    @Test
    public void testGetPopular() {
        Film film = filmStorage.create(getTestFilm());
        Film film2 = filmStorage.create(getTestFilm2());

        User user = userStorage.create(getTestUser());
        User user2 = userStorage.create(getTestUser2());

        List<Film> filmWithoutLikes = filmStorage.getMostPopular(2);
        assertFalse(filmWithoutLikes.isEmpty());
        assertEquals(2, filmWithoutLikes.size());
        assertEquals(1, filmWithoutLikes.get(0).getId());
        assertEquals(2, filmWithoutLikes.get(1).getId());

        filmStorage.like(2, 1);
        filmStorage.like(2, 2);

        List<Film> popularFilm = filmStorage.getMostPopular(1);
        assertFalse(popularFilm.isEmpty());
        assertEquals(2, popularFilm.get(0).getId());
    }

    @Test
    public void testGetFilmById() {
        Film film = filmStorage.create(getTestFilm());

        Film receivedFilm = filmStorage.getById(1);
        assertEquals(film, receivedFilm);
    }

    @Test
    public void shouldThrowExceptionIfIncorrectId() {
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        filmStorage.getById(3);
                    }
                });
    }

    @Test
    public void testGetAll() {
        Film film = filmStorage.create(getTestFilm());
        Film film2 = filmStorage.create(getTestFilm2());

        List<Film> films = filmStorage.getAll();
        assertEquals(2,films.size());
        assertEquals(film, films.get(0));
        assertEquals(film2, films.get(1));
    }

    @Test
    public void testLike() {
        Film film = filmStorage.create(getTestFilm());
        User user = userStorage.create(getTestUser());
        filmStorage.like(user.getId(), 1);
        Set<Long> likes = filmStorage.getById(film.getId()).getLikes();
        assertFalse(likes.isEmpty());
        assertTrue(likes.contains(1L));
    }

    @Test
    public void testDeleteLike() {
        Film film = filmStorage.create(getTestFilm());
        User user = userStorage.create(getTestUser());
        filmStorage.like(user.getId(), 1);
        Set<Long> likes = filmStorage.getById(film.getId()).getLikes();
        assertFalse(likes.isEmpty());
        assertTrue(likes.contains(1L));

        filmStorage.deleteLike(user.getId(), 1);
        Set<Long> noLikes = filmStorage.getById(film.getId()).getLikes();
        assertTrue(noLikes.isEmpty());
    }

    @Test
    public void testUpdateGenres() {
        Set<Genre> newGenres = new HashSet<>();

    }

}
