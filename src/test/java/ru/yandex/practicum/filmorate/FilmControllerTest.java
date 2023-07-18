package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmControllerTest {
    @Autowired
    private FilmController filmController;
    @Autowired
    private FilmStorage filmStorage;
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
        assertThat(filmController).isNotNull();
    }

    @Test
    void validateIfAllDataCorrect() throws Exception {
        Film film = filmStorage.create(getTestFilm());
        mockMvc.perform(get("/films")
                .contentType("application/json"))
                .andDo(h -> assertEquals(200, h.getResponse().getStatus()));

        mockMvc.perform(get("/films/1")
                .contentType("application/json"))
                .andDo(h -> assertEquals(200, h.getResponse().getStatus()))
                .andExpect(content().json(objectMapper.writeValueAsString(film)));
    }

    @Test
    void validateIfDurationNegative() throws Exception {
        Film inValidFilm = getTestFilm();
        inValidFilm.setDuration(-100);
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(inValidFilm)))
                        .andDo(h -> {assertEquals(400, h.getResponse().getStatus());
                        }
                );
    }

    @Test
    void validateIfNoName() throws Exception {
        Film inValidFilm = getTestFilm();
        inValidFilm.setName("");
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(inValidFilm)))
                        .andDo(h -> {assertEquals(400, h.getResponse().getStatus());
                        }
                );
    }

    @Test
    void validateIfReleaseDateEarlierThan28Dec1895() throws Exception {
        Film inValidFilm = getTestFilm();
        inValidFilm.setReleaseDate(LocalDate.of(1700, 3, 25));

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(inValidFilm)))
                        .andDo(h -> {assertEquals(400, h.getResponse().getStatus());
                        }
                );
    }

    @Test
    void validationWithEmptyBody() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(""))
                        .andDo(h -> {assertEquals(500, h.getResponse().getStatus());
                        }
                );
    }

    @Test
    void addLike() throws Exception {
        Film film = filmStorage.create(getTestFilm());
        User user = userStorage.create(getTestUser());

        mockMvc.perform(MockMvcRequestBuilders.put("/films/1/like/1")
                .contentType("application/json"))
                .andDo(h -> assertEquals(200, h.getResponse().getStatus()));
        assertEquals(Set.of(user.getId()), film.getLikes());
    }

    @Test
    void addLikeIfIncorrectFilmIdOrUserId() throws Exception {
        Film film = filmStorage.create(getTestFilm());
        User user = userStorage.create(getTestUser());

        mockMvc.perform(MockMvcRequestBuilders.put("/films/2/like/1")
                .contentType("application/json"))
                .andDo(h -> assertEquals(404, h.getResponse().getStatus()));

        mockMvc.perform(MockMvcRequestBuilders.put("/films/1/like/2")
                .contentType("application/json"))
                .andDo(h -> assertEquals(404, h.getResponse().getStatus()));
    }

    @Test
    void deleteLike() throws Exception {
        Film film = filmStorage.create(getTestFilm());
        User user = userStorage.create(getTestUser());

        filmStorage.like(film.getId(), user.getId());
        mockMvc.perform(MockMvcRequestBuilders.delete("/films/1/like/1")
                .contentType("application/json"))
                .andDo(h -> assertEquals(200, h.getResponse().getStatus()));
        assertTrue(film.getLikes().isEmpty());
    }

    @Test
    void deleteLikeIfIncorrectUserIdOrFilmId() throws Exception {
        Film film = filmStorage.create(getTestFilm());
        User user = userStorage.create(getTestUser());

        filmStorage.like(film.getId(), user.getId());

        mockMvc.perform(MockMvcRequestBuilders.put("/films/2/like/1")
                .contentType("application/json"))
                .andDo(h -> assertEquals(404, h.getResponse().getStatus()));

        mockMvc.perform(MockMvcRequestBuilders.put("/films/1/like/2")
                .contentType("application/json"))
                .andDo(h -> assertEquals(404, h.getResponse().getStatus()));
    }

    @Test
    void getPopularFilms() throws Exception {
        Film film = filmStorage.create(getTestFilm());
        Film popularFilm = filmStorage.create(getTestFilm2());
        User user = userStorage.create(getTestUser());
        User user2 = userStorage.create(getTestUser2());

        filmStorage.like(popularFilm.getId(), user.getId());
        filmStorage.like(popularFilm.getId(), user2.getId());

        mockMvc.perform(get("/films/popular")
                .contentType("application/json"))
                .andDo(h -> assertEquals(200, h.getResponse().getStatus()))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(popularFilm, film))));
    }

    private Film getTestFilm() {
        return Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
    }

    private Film getTestFilm2() {
        return Film.builder()
                .name("nisi2 eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1970, 3, 25))
                .duration(100)
                .build();
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