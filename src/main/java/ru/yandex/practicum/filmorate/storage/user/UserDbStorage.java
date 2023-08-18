package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        long id = simpleJdbcInsert.executeAndReturnKey(toMap(user)).longValue();
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "update users set email = ?, login = ?, name = ?, birthday = ? where id = ?";
        if (jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), user.getId()) < 1) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден.");
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM users";
        return new ArrayList<>(jdbcTemplate.query(sql, this::makeUser));
    }

    @Override
    public User getById(long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(sql, this::makeUser, id);
        } catch (Exception e) {
            log.info("User with id {} not found", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден.");
        }
        return user;
    }

    @Override
    public void addAsFriend(long userId, long friendId) {
        String sqlUpdate = "UPDATE friendships SET is_confirmed = true WHERE user_id = ? AND friend_id = ?";
        String sqlInsert = "INSERT INTO friendships(user_id, friend_id, is_confirmed) VALUES (?, ?, ?)";
        try {
            boolean isFriendShipPresent = jdbcTemplate.update(sqlUpdate, friendId, userId) > 0;
            if (isFriendShipPresent) {
                jdbcTemplate.update(sqlInsert, userId, friendId, true);
            } else {
                jdbcTemplate.update(sqlInsert, userId, friendId, false);
            }
        } catch (Exception e) {
            log.info("User not found");
            throw new NotFoundException("Пользователь не найден.");
        }
    }

    @Override
    public void deleteFromFriends(long userId, long friendId) {
        String sqlDel = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        String sqlUpdateStatus = "UPDATE friendships SET is_confirmed = false WHERE user_id = ? AND friend_id = ?";
        if (jdbcTemplate.update(sqlDel, userId, friendId) < 1) {
            throw new NotFoundException("Пользователь не найден.");
        } else {
            jdbcTemplate.update(sqlUpdateStatus, friendId, userId);
        }
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        List<User> usersFriends = getFriends(userId);
        List<User> otherUsersFriends = getFriends(otherId);

        return usersFriends.stream()
                .filter(otherUsersFriends::contains)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getFriends(long id) {
        String sql = "SELECT * FROM users WHERE id IN (SELECT friend_id FROM friendships WHERE user_id = ?)";
        List<User> friends = new ArrayList<>();
        try {
            friends = jdbcTemplate.query(sql, this::makeUser, id);
        } catch (Exception e) {
            throw new NotFoundException("Пользователь не найден.");
        }
      return friends;
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        user.getFriends().addAll(loadFriendsIds(rs.getLong("id")));

       return user;
    }

    private Map<String, Object> toMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());

        return values;
    }

    private Set<Long> loadFriendsIds(long userId) {
        String sql = "SELECT friend_id FROM friendships WHERE user_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Long.class, userId));
    }

}
