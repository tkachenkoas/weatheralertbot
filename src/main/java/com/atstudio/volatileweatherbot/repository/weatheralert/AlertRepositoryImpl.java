package com.atstudio.volatileweatherbot.repository.weatheralert;

import com.atstudio.volatileweatherbot.models.domain.WeatherAlert;
import com.atstudio.volatileweatherbot.repository.AbstractJdbcRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static com.atstudio.volatileweatherbot.repository.columns.EntityColumnsUtils.joinColumnNames;
import static com.atstudio.volatileweatherbot.repository.weatheralert.WeatherAlertColumns.LOCATION_CODE;
import static com.atstudio.volatileweatherbot.repository.weatheralert.WeatherAlertColumns.WEATHER_ALERTS_TABLE;

@Repository
public class AlertRepositoryImpl extends AbstractJdbcRepository<WeatherAlert> implements AlertRepository {

    @Override
    public void save(WeatherAlert alert) {
        createIfNotExist(alert, WEATHER_ALERTS_TABLE, WeatherAlertColumns.values());
    }

    @Override
    public List<WeatherAlert> getForLocation(String locationCode) {
        return jdbcTemplate.query(
                " SELECT " + joinColumnNames(",", WeatherAlertColumns.values()) +
                     " FROM " + WEATHER_ALERTS_TABLE +
                     " WHERE " + LOCATION_CODE + "=:locationCode",
                paramSource(Collections.singletonMap("locationCode", locationCode)),
                rowMapper
        );
    }

    private static RowMapper<WeatherAlert> rowMapper = (resultSet, i) ->  {
        WeatherAlert alert = new WeatherAlert();
        for (WeatherAlertColumns column: WeatherAlertColumns.values()) {
            column.setProp(alert, resultSet);
        }
        return alert;
    };
}
