package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Qualifier("GenreDbStorage")
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
        boolean isUpdated = jdbcTemplate.update(sqlQuery, genre.getName()) > 0;
        if (!isUpdated) {
            throw new NotFoundException("Жанр c id " + genre.getId() + " не найден");
        }
        return genre;
    }

    public List<Genre> getAll() {
        String sql = "SELECT id, name FROM genres";
        List<Genre> genres = jdbcTemplate.query(sql, this::makeGenre);
        if (genres.isEmpty()) {
            return new ArrayList<>();
        } else {
            return genres;
        }
    }

    @Override
    public Genre getById(long id) {
        checkGenreId(id);
        String sql = "SELECT id, name FROM genres WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, this::makeGenre, id);
    }

    private Map<String, Object> toMap(Genre genre) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", genre.getName());
        return values;
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getLong("id"), rs.getString("name") );
    }

    public void checkGenreId(long genreId) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        List<Genre> genre = jdbcTemplate.query(sql, this::makeGenre, genreId);

        if (genre.isEmpty()) {
            log.info("Genre with id {} not found", genreId);
            throw new NotFoundException("Жанр с id " + genreId + " не найден");
        }
    }
}
