package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidationTest {

    private UserController controller;
    private User user;

    @BeforeEach
    public void beforeEach() {
        controller = new UserController();
        user = new User();
        user.setEmail("test@test.ru");
        user.setLogin("TestLogin");
        user.setName("TestName");
        user.setBirthday(LocalDate.of(1985, 12, 31));
    }

    @Test
    public void shouldThrowValidationExceptionIfEmailIsEmpty() {
        user.setEmail("");
        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        controller.createUser(user);
                    }
                });
        assertEquals("Электронная почта не может быть пустой", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionIfEmailDoesNotContainAt() {
        user.setEmail("Test.newTest");
        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        controller.createUser(user);
                    }
                });
        assertEquals("Электронная почта должна содержать @)", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionIfLoginIsBlank() {
        user.setLogin("Test test");
        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        controller.createUser(user);
                    }
                });
        assertEquals("Логин не может содержать пробелы", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionIfLoginIsEmpty() {
        user.setLogin("");
        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        controller.createUser(user);
                    }
                });
        assertEquals("Логин не может быть пустым", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionIfBirthdayAfterNow() {
        user.setBirthday(LocalDate.of(2023, 12, 31));
        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        controller.createUser(user);
                    }
                });
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    public void loginShouldBecomeNameIfNameIsEmpty() {
        user.setName("");
        User createdUser = controller.createUser(user);
        assertEquals(user.getLogin(), createdUser.getName());
    }

    @Test
    public void shouldThrowExceptionIfAllFieldsNull() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        controller.createUser(new User());
                    }
                });
    }
}


