package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Qualifier("MpaDbStorage")
@Slf4j
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa create(Mpa rating) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("users")
                .withTableName("ratings")
                .usingGeneratedKeyColumns("id");
        long id = simpleJdbcInsert.executeAndReturnKey(toMap(rating)).longValue();
        rating.setId(id);
        return rating;
    }

    @Override
    public Mpa update(Mpa rating) {
        String sqlQuery = "UPDATE ratings SET name = ? WHERE id = ?";
        boolean isUpdated = jdbcTemplate.update(sqlQuery, rating.getName()) > 0;
        if (!isUpdated) {
            throw new NotFoundException("Жанр c id " + rating.getId() + " не найден");
        }
        return rating;
    }

    @Override
    public List<Mpa> getAll() {
        String sql = "SELECT * FROM ratings";
        List<Mpa> ratings = jdbcTemplate.query(sql, this::makeRating);
        if (ratings.isEmpty()) {
            return new ArrayList<>();
        } else {
            return ratings;
        }
    }

    @Override
    public Mpa getById(long id) {
        checkMpaId(id);
        String sql = "SELECT id, name FROM ratings WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, this::makeRating, id);
    }

    private Map<String, Object> toMap(Mpa rating) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", rating.getName());
        return values;
    }

    private Mpa makeRating(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getLong("id"), rs.getString("name"));
    }

    public void checkMpaId(long mpaId) {
        String sql = "SELECT * FROM ratings WHERE id = ?";
        List<Mpa> mpa = jdbcTemplate.query(sql, this::makeRating, mpaId);

        if (mpa.isEmpty()) {
            log.info("Rating with id {} not found", mpaId);
            throw new NotFoundException("Рейтинг с id " + mpaId + " не найден");
        }
    }
}
