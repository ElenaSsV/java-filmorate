package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private UserController userController;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
        assertThat(userController).isNotNull();
    }

    @SneakyThrows
    @Test
    void validateWhenAllDataCorrect() {
        User user = User.builder()
                .login("testLogin")
                .name("Test Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1990, 8, 20))
                .build();
        String validUser = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(validUser)).andDo(h -> assertEquals(200, h.getResponse().getStatus()));

        mockMvc.perform(get("/users")
                .contentType("application/json")).andDo(h ->
                assertEquals(200, h.getResponse().getStatus()));
    }

    @SneakyThrows
    @Test
    void validateWhenEmailIsInvalid() {
        String inValidUser = objectMapper.writeValueAsString(User.builder()
                .login("testLogin")
                .name("Test Name")
                .email("mail.ru")
                .birthday(LocalDate.of(1990, 8, 20))
                .build());
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(inValidUser)).andDo(
                h -> {
                    assertEquals(400, h.getResponse().getStatus());
                }
        );
    }

    @SneakyThrows
    @Test
    void validateWhenEmailIfBirthdayInFuture() {
        String inValidUser = objectMapper.writeValueAsString(User.builder()
                .login("testLogin")
                .name("Test Name")
                .email("mail.ru")
                .birthday(LocalDate.of(2024, 8, 20))
                .build());
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(inValidUser)).andDo(
                h -> {
                    assertEquals(400, h.getResponse().getStatus());
                }
        );
    }

    @SneakyThrows
    @Test
    void shouldCreateUserIfNameIsEmpty() {
        String inValidUser = objectMapper.writeValueAsString(User.builder()
                .login("testLogin")
                .name("")
                .email("mail.ru")
                .birthday(LocalDate.of(2024, 8, 20))
                .build());
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(inValidUser)).andDo(
                h -> {
                    assertEquals(400, h.getResponse().getStatus());
                }
        );
    }
}
