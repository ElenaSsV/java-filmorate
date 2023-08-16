package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        log.info("Creating film {}", film);
        long id = simpleJdbcInsert.executeAndReturnKey(toMap(film)).longValue();
        film.setId(id);

        updateGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        checkFilmId(film.getId());
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, " +
                "rating_id = ? WHERE id = ?";
        log.info("Updating film {}", film);
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        updateGenres(film);
        updateLikes(film);

        return getById(film.getId());
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name AS rating_name " +
                "FROM films AS f JOIN ratings AS r ON f.RATING_ID = r.ID ";
        return new ArrayList<>(jdbcTemplate.query(sql, this::makeFilm));
    }

    @Override
    public Film getById(long id) {
        checkFilmId(id);
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name " +
                "AS rating_name FROM films AS f JOIN ratings AS r ON f.RATING_ID = r.ID WHERE f.id = ?";
        log.info("Received filmId {}", id);
        return jdbcTemplate.queryForObject(sql, this::makeFilm, id);
    }

    @Override
    public void like(long filmId, long userId) {
        checkFilmId(filmId);
        checkUserId(userId);
        String sql = "INSERT INTO likes (film_id, user_id) VALUES(?, ?)";
        log.info("Adding like to film {} from user {}", filmId, userId);
        jdbcTemplate.update(sql, filmId, userId);

    }

    @Override
    public void deleteLike(long filmId, long userId) {
        checkFilmId(filmId);
        checkUserId(userId);
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        log.info("Deleting like from film {} from user {}", filmId, userId);
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getMostPopular(int count) {
        return getAll().stream()
                .sorted(Comparator.comparing(Film::getLikesQty).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {

        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setGenres(loadGenres(rs.getLong("id")));
        film.setMpa(new Mpa(rs.getLong("rating_id"), rs.getString("rating_name")));
        film.getLikes().addAll(getWhoLiked(rs.getLong("id")));

        return film;
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("rating_id", film.getMpa().getId());
        return values;
    }

    private Set<Long> getWhoLiked(long filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Long.class, filmId));
    }

    private void updateGenres(Film film) {
        if (film.getGenres() == null) {
            return;
        }
        String delSql = "DELETE FROM film_genres WHERE film_id = ?";
        String addSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        jdbcTemplate.update(delSql, film.getId());

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(addSql, film.getId(), genre.getId());
        }
    }

    private Set<Genre> loadGenres(long filmId) {
        String sql = "SELECT DISTINCT genre_id FROM film_genres WHERE film_id = ? ";

        return jdbcTemplate.queryForList(sql, Long.class, filmId).stream()
                .map(genreStorage::getById)
                .collect(Collectors.toSet());
    }

    private void updateLikes(Film film) {
        if (film.getLikes().isEmpty()) {
            return;
        }
        String delSql = "DELETE FROM likes WHERE film_id = ?";
        String addSql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";

        jdbcTemplate.update(delSql, film.getId());

        for (Long userId : film.getLikes()) {
            jdbcTemplate.update(addSql, film.getId(), userId);
        }
    }

    private void checkUserId(long userId) {
        User user = userStorage.getById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь id " + userId + " не найден");
        }
    }

    private void checkFilmId(long filmId) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name " +
                "AS rating_name FROM films AS f JOIN ratings AS r ON f.RATING_ID = r.ID WHERE f.id = ?";
        List<Film> film = jdbcTemplate.query(sql, this::makeFilm, filmId);

        if (film.isEmpty()) {
            log.info("Film with id {} not found", filmId);
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
    }
}
