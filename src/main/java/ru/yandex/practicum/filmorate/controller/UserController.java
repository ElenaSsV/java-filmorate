package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController extends Controller<User> {

    private long id = 1L;

    public void validateData(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.info("No user name is received");
            user.setName(user.getLogin());
            log.info("User name is set to: {}", user.getLogin());
        }
    }
}
