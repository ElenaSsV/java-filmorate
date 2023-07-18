package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.CommonStorage;

import java.util.List;

public interface UserStorage extends CommonStorage<User> {

    void addAsFriend(long userId, long friendId);

    void deleteFromFriends(long userId, long friendId);

    List<User> getCommonFriends(long userId, long otherId);

    List<User> getFriends(long id);
}
