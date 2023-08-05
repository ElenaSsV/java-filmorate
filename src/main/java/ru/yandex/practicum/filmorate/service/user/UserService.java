package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.AbstractService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserService extends AbstractService<User, UserStorage> {

   @Autowired
    public UserService(UserStorage storage) {
        super(storage);
    }

    public void addAsFriend(long userId, long friendId) {
       storage.addAsFriend(userId, friendId);
    }

    public List<User> getFriends(long id) {
       return storage.getFriends(id);
    }

    public void deleteFromFriends(long userId, long friendId) {
       storage.deleteFromFriends(userId, friendId);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
       return storage.getCommonFriends(userId, otherId);
    }

}
