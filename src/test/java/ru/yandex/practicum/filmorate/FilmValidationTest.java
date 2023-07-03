package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmValidationTest {
    private FilmController controller;
    private Film newFilm;

    @BeforeEach
    public void beforeEach() {
        controller = new FilmController();
        newFilm = new Film();
        newFilm.setName("Test name");
        newFilm.setDescription("Test new film");
        newFilm.setReleaseDate(LocalDate.of(2022, 12, 31));
        newFilm.setDuration(90);
    }

    @Test
    public void shouldThrowValidationExceptionIfNameIsEmpty() {
        newFilm.setName("");

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        controller.createFilm(newFilm);
                    }
                });
        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionIfReleaseDateIsBefore28Dec1895() {
        newFilm.setReleaseDate(LocalDate.of(1799, 12, 31));
        LocalDate earliestReleaseDate = LocalDate.of(1895, 12, 28);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        controller.createFilm(newFilm);
                    }
                });
        assertEquals("Дата релиза не может быть раньше " +   earliestReleaseDate + ".", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionIfDurationIsNegative() {
        newFilm.setDuration(-1);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        controller.createFilm(newFilm);
                    }
                });
        assertEquals("Продолжительность фильма должна быть положительной", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionIfDescriptionIsLongerThan200Symbols() {

        List<String> list = Collections.nCopies(201, "T");
        newFilm.setDescription(list.toString());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        controller.createFilm(newFilm);
                    }
                });
        assertEquals("Максимальная длина описания фильма - 200", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfAllFieldsNull() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        controller.createFilm(new Film());
                    }
                });
    }
}

