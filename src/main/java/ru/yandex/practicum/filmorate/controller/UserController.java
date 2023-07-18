package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController extends AbstractController<User, UserService> {

    @Autowired
    public UserController(UserService service) {
        super(service);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addAsFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        log.info("Received user id {} and friendId {}", id, friendId);
        service.addAsFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        service.deleteFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Long id, @PathVariable("otherId") Long otherId) {
        return service.getCommonFriends(id, otherId);
    }

   @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable("id") Long id) {
       return service.getFriends(id);
   }
}
