package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {

    private final GenreDbStorage genreStorage;

    @Test
    public void testGetById() {
        Genre genre = genreStorage.getById(1);
        assertEquals("Комедия", genre.getName());
    }

    @Test
    public void shouldThrowExceptionIfIncorrectId() {
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        genreStorage.getById(10);
                    }
                });
    }

    @Test
    public void testGetAll() {
        List<Genre> genres = genreStorage.getAll();
        assertEquals(6, genres.size());
    }
}
