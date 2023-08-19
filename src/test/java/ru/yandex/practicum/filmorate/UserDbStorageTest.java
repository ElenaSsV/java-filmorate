package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserDbStorageTest {

    private final UserDbStorage userStorage;

   public User getTestUser() {
       User user = new User();
       user.setLogin("testLogin");
       user.setName("Test Name");
       user.setEmail("mail@mail.ru");
       user.setBirthday(LocalDate.of(1990, 8, 20));
       return user;
   }

   public User getTesUser2() {
       User user2 = new User();
       user2.setLogin("test2Login");
       user2.setName("Test2 Name");
       user2.setEmail("mail2@mail.ru");
       user2.setBirthday(LocalDate.of(1990, 8, 20));
       return user2;
   }

   public User getTestUser3() {
       User user3 = new User();
       user3.setLogin("test3Login");
       user3.setName("Test3 Name");
       user3.setEmail("mail3@mail.ru");
       user3.setBirthday(LocalDate.of(1990, 8, 20));
       return user3;
    }


    @Test
    public void testFindUserById() {
       userStorage.create(getTestUser());
       Optional<User> userOptional = Optional.ofNullable(userStorage.getById(1));
       assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void shouldThrowExceptionIfIncorrectId() {
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        userStorage.getById(4);
                    }
                });
    }

    @Test
    public void testGetAll() {
       User user = getTestUser();
       User user2 = getTesUser2();

       userStorage.create(user);
       userStorage.create(user2);
       List<User> users = userStorage.getAll();
       assertEquals(user, users.get(0));
    }

    @Test
    public void testAddFriend() {
       User user = userStorage.create(getTestUser());
       User user2 = userStorage.create(getTesUser2());

       userStorage.addAsFriend(user.getId(), user2.getId());
       List<User> friendsOfUser1 = userStorage.getFriends(user.getId());
       assertFalse(friendsOfUser1.isEmpty());

       List<User> friendsOfUser2 = userStorage.getFriends(user2.getId());
       assertTrue(friendsOfUser2.isEmpty());

       userStorage.addAsFriend(user2.getId(), user.getId());

       List<User> user1friends = userStorage.getFriends(user.getId());
       assertFalse(user1friends.isEmpty());

       assertEquals(user2.getId(), user1friends.get(0).getId());

       List<User> user2friends = userStorage.getFriends(user2.getId());
       assertFalse(user2friends.isEmpty());

       assertEquals(user.getId(), user2friends.get(0).getId());
    }

    @Test
    public void shouldThrowExceptionIfAddedFriendWithIncorrectId() {
        User user = userStorage.create(getTestUser());
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        userStorage.addAsFriend(user.getId(), -2);
                    }
                });
    }

    @Test
    public void testDeleteFriend() {
       User user = getTestUser();
       User user2 = getTesUser2();
       User user3 = getTestUser3();
       userStorage.create(user);
       userStorage.create(user2);
       userStorage.create(user3);

       userStorage.addAsFriend(user.getId(), user2.getId());
       userStorage.addAsFriend(user.getId(), user3.getId());
       userStorage.addAsFriend(user2.getId(), user3.getId());

       List<User> userFriends = userStorage.getFriends(user.getId());
       assertEquals(2, userFriends.size());

       userStorage.deleteFromFriends(user.getId(), user2.getId());
       List<User> userFriendsAfterDel = userStorage.getFriends(user.getId());
       assertEquals(1, userFriendsAfterDel.size());
       assertEquals(3, userFriendsAfterDel.get(0).getId());
    }

    @Test
    public void testGetCommonFriends() {
       User user = getTestUser();
       User user2 = getTesUser2();
       User user3 = getTestUser3();
       userStorage.create(user);
       userStorage.create(user2);
       userStorage.create(user3);

       userStorage.addAsFriend(user.getId(), user3.getId());
       List<User> noCommonFriends = userStorage.getCommonFriends(user.getId(), user2.getId());
       assertTrue(noCommonFriends.isEmpty());

       userStorage.addAsFriend(user2.getId(), user3.getId());

       List<User> commonFriends = userStorage.getCommonFriends(user.getId(), user2.getId());
       assertFalse(commonFriends.isEmpty());
       assertEquals(3, commonFriends.get(0).getId());
    }

}

