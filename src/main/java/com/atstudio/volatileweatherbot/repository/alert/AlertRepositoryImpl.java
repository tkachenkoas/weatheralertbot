package com.atstudio.volatileweatherbot.repository.alert;

import com.atstudio.volatileweatherbot.models.WeatherAlert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static com.atstudio.volatileweatherbot.repository.alert.WeatherAlertColumns.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

public class AlertRepositoryImpl implements AlertRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AlertRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public WeatherAlert save(WeatherAlert alert) {
        WeatherAlertColumns[] allColumns = WeatherAlertColumns.values();
        jdbcTemplate.update(
                " INSERT INTO " + TABLE_NAME +
                        "(" + joinColumnNames(",", allColumns) + ")" +
                        " VALUES (" + range(1, allColumns.length + 1).mapToObj(c -> "?").collect(joining(",")) +") ",
                getFieldsFromObject(alert, allColumns)
        );
        return alert;
    }
}
