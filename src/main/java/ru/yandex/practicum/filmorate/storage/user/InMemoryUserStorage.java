package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage extends AbstractStorage<User> implements UserStorage {

    @Override
    public void validateData(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.info("No user name is received");
            user.setName(user.getLogin());
            log.info("User name is set to: {}", user.getLogin());
        }
    }

    @Override
    public void addAsFriend(long userId, long friendId) {
        checkUserId(userId);
        checkUserId(friendId);

        User user = storage.get(userId);
        User anotherUser = storage.get(friendId);

        user.getFriends().add(friendId);
        log.info("User with id {} is added as friend to User with id {}", friendId, userId);
        anotherUser.getFriends().add(userId);
        log.info("User with id {} is added as friend to User with id {}", userId, friendId);
    }

    @Override
    public List<User> getFriends(long id) {
        checkUserId(id);
        return storage.get(id).getFriends().stream()
                .map(storage ::get)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFromFriends(long userId, long friendId) {
        checkUserId(userId);
        checkUserId(friendId);
        storage.get(userId).getFriends().remove(friendId);
        log.info("User with id {} deleted from friends of User with id {}", friendId, userId);
        storage.get(friendId).getFriends().remove(userId);
        log.info("User with id {} deleted from friends of User with id {}", userId, friendId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        checkUserId(userId);
        checkUserId(otherId);

        Set<Long> idsOfUserFriends = storage.get(userId).getFriends();
        Set<Long> idsOfOtherUserFriends = storage.get(otherId).getFriends();

        return idsOfUserFriends.stream()
                .filter(idsOfOtherUserFriends::contains)
                .map(storage::get)
                .collect(Collectors.toList());
    }

    private void checkUserId(long id) {
        if (!storage.containsKey(id)) {
            log.info("Received incorrect user id {}", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }
}
