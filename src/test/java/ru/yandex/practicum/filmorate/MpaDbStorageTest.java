package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorageTest {

    private final MpaDbStorage mpaStorage;

    @Test
    public void testGetById() {
        Mpa mpa = mpaStorage.getById(1);
        assertEquals("G", mpa.getName());
    }

    @Test
    public void shouldThrowExceptionIfIncorrectId() {
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        mpaStorage.getById(10);
                    }
                });
    }

    @Test
    public void testGetAll() {
        List<Mpa> mpas = mpaStorage.getAll();
        assertEquals(5, mpas.size());
    }

}
