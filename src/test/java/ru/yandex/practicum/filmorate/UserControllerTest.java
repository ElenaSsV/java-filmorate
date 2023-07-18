package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {
    @Autowired
    private UserController userController;
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
        assertThat(userController).isNotNull();
    }

    @Test
    void validateWhenAllDataCorrect() throws Exception {
        User user = userStorage.create(getTestUser());

        mockMvc.perform(get("/users")
                .contentType("application/json"))
                .andDo(h -> assertEquals(200, h.getResponse().getStatus()))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(user))));

        mockMvc.perform(get("/users/1")
                .contentType("application/json")).andDo(h ->
                assertEquals(200, h.getResponse().getStatus()))
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    void validateWhenEmailIsInvalid() throws Exception {
        User inValidUser = getTestUser();
        inValidUser.setEmail("mail.mail.ru");

        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(inValidUser)))
                .andDo(h -> {assertEquals(400, h.getResponse().getStatus());
                }
        );
    }

    @Test
    void validateWhenEmailIfBirthdayInFuture() throws Exception {
        User inValidUser = getTestUser();
        inValidUser.setBirthday(LocalDate.of(2024, 8, 20));

        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(inValidUser)))
                .andDo(h -> {assertEquals(400, h.getResponse().getStatus());
                }
        );
    }

    @Test
    void shouldCreateUserIfNameIsEmpty() throws Exception {
        User user = getTestUser();
        user.setName("");

        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)))
                .andDo(h -> {assertEquals(200, h.getResponse().getStatus());
                }
        );
    }

    @Test
    void addAsFriend() throws Exception {
        User user = userStorage.create(getTestUser());
        User friend = userStorage.create(getTestUser2());

        mockMvc.perform(put("/users/1/friends/2")
                .contentType("application/json"))
                .andDo(h -> assertEquals(200, h.getResponse().getStatus()));
        assertEquals(Set.of(user.getId()), friend.getFriends());
        assertEquals(Set.of(friend.getId()), user.getFriends());
    }

    @Test
    void addAsFriendIfNoSuchUser() throws Exception {
        User user = userStorage.create(getTestUser());

        mockMvc.perform(put("/users/1/friends/5")
                .contentType("application/json"))
                .andDo(h -> assertEquals(404, h.getResponse().getStatus()));
    }

    @Test
    void deleteFromFriendsIfNoSuchUser() throws Exception {
        User user = userStorage.create(getTestUser());

        mockMvc.perform(delete("/users/1/friends/5")
                .contentType("application/json"))
                .andDo(h -> assertEquals(404, h.getResponse().getStatus()));
    }

    @Test
    void getFriendsIfSetIsEmpty() throws Exception {
        User user = userStorage.create(getTestUser());

        mockMvc.perform(get("/users/1/friends")
                .contentType("application/json")).andDo(h ->
                assertEquals(200, h.getResponse().getStatus()));
    }

    @Test
    void getCommonFriendsIfNoCommonFriends() throws Exception {
        User user = userStorage.create(getTestUser());
        User user2 = userStorage.create(getTestUser2());

        mockMvc.perform(get("/users/1/friends/common/2")
                .contentType("application/json")).andDo(h ->
                assertEquals(200, h.getResponse().getStatus()));
    }

    private User getTestUser() {
        return User.builder()
                .login("testLogin")
                .name("Test Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1990, 8, 20))
                .build();
    }

    private User getTestUser2() {
        return User.builder()
                .login("testLogin2")
                .name("Test Name2")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1991, 8, 20))
                .build();
    }
}
