package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Primary
@Slf4j
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre create(Genre genre) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("users")
                .withTableName("genres")
                .usingGeneratedKeyColumns("id");
        long id = simpleJdbcInsert.executeAndReturnKey(toMap(genre)).longValue();
        genre.setId(id);
        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        String sqlQuery = "UPDATE genres SET name = ? WHERE id = ?";
        if (jdbcTemplate.update(sqlQuery, genre.getName()) < 1) {
            log.info("Genre with id {} not found", genre.getId());
            throw new NotFoundException("Жанр c id " + genre.getId() + " не найден");
        }
        return genre;
    }

    public List<Genre> getAll() {
        String sql = "SELECT id, name FROM genres";
        return new ArrayList<>(jdbcTemplate.query(sql, this::makeGenre));
    }

    @Override
    public Genre getById(long id) {
        String sql = "SELECT id, name FROM genres WHERE id = ?";
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(sql, this::makeGenre, id);
        } catch (Exception e) {
            log.info("Genre with id {} not found", id);
            throw new NotFoundException("Жанр c id " + id + " не найден");
        }
        return genre;
    }

    private Map<String, Object> toMap(Genre genre) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", genre.getName());
        return values;
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getLong("id"), rs.getString("name"));
    }
}
