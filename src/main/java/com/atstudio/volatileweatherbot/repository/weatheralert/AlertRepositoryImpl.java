package com.atstudio.volatileweatherbot.repository.weatheralert;

import com.atstudio.volatileweatherbot.models.domain.WeatherAlert;
import com.atstudio.volatileweatherbot.repository.AbstractJdbcRepository;
import com.google.common.collect.ImmutableMap;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

import static com.atstudio.volatileweatherbot.repository.RepoJdbcUtils.generateUuid;
import static com.atstudio.volatileweatherbot.repository.RepoJdbcUtils.toTimeStamp;
import static com.atstudio.volatileweatherbot.repository.columns.EntityColumns.colName;
import static com.atstudio.volatileweatherbot.repository.columns.EntityColumnsUtils.joinColumnNames;
import static com.atstudio.volatileweatherbot.repository.location.LocationColumns.*;
import static com.atstudio.volatileweatherbot.repository.weatheralert.WeatherAlertColumns.*;
import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Collections.singletonMap;

@Repository
public class AlertRepositoryImpl extends AbstractJdbcRepository<WeatherAlert> implements AlertRepository {

    @Override
    public WeatherAlert save(WeatherAlert alert) {
        alert.setUuid(generateUuid());
        createIfNotExist(alert, WEATHER_ALERTS_TABLE, WeatherAlertColumns.values());
        return alert;
    }

    @Override
    public List<WeatherAlert> getForLocation(String locationCode) {
        return jdbcTemplate.query(
                " SELECT " + joinColumnNames(",", WeatherAlertColumns.values()) +
                        " FROM " + WEATHER_ALERTS_TABLE +
                        " WHERE " + colName(ALERT_LOCATION_CODE) + "=:locationCode",
                singletonMap("locationCode", locationCode),
                rowMapper
        );
    }

    @Override
    public List<WeatherAlert> getTriggeredAlerts() {
        StringBuilder queryBuilder = new StringBuilder()
                .append(format(" SELECT %s \n", joinColumnNames(",", WeatherAlertColumns.values())))
                .append(format(" FROM %s al", WEATHER_ALERTS_TABLE))
                .append(format(" JOIN %s loc ON al.%s = loc.%s \n",
                        LOCATIONS_TABLE_NAME, colName(ALERT_LOCATION_CODE), colName(LOCATION_CODE)))
                .append(format(" WHERE ( al.%1$s is NULL OR al.%1$s = timezone(loc.%2$s, :now)::date )\n",
                        NEXT_CHECK_DATE_COLUMN, colName(TIMEZONE)))
                .append(format(" AND al.%s < timezone(loc.%s, :now)::time",
                        colName(ALERT_TIME), colName(TIMEZONE)));

        return jdbcTemplate.query(
                queryBuilder.toString(),
                ImmutableMap.<String, Object>builder()
                        .put("now", toTimeStamp(Instant.now()))
                        .build(),
                rowMapper
        );
    }

    @Override
    public void postponeAlertForTomorrow(WeatherAlert alert) {
        StringBuilder queryBuilder = new StringBuilder()
                .append(format(" UPDATE %s al \n", WEATHER_ALERTS_TABLE))
                .append(format(" SET %s = timezone(loc.%2$s, :date)::date \n",
                        NEXT_CHECK_DATE_COLUMN, colName(TIMEZONE)))
                .append(format(" FROM %s loc WHERE al.%s = loc.%s \n",
                        LOCATIONS_TABLE_NAME, colName(ALERT_LOCATION_CODE), colName(LOCATION_CODE)))
                .append(format(" AND al.%s = :uuid ", colName(UUID)));

        jdbcTemplate.update(
                queryBuilder.toString(),
                ImmutableMap.<String, Object>builder()
                        .put("uuid", alert.getUuid())
                        .put("date", toTimeStamp(Instant.now().plus(1, DAYS)))
                        .build()
        );
    }

    private static RowMapper<WeatherAlert> rowMapper = (resultSet, i) -> {
        WeatherAlert alert = new WeatherAlert();
        for (WeatherAlertColumns column: WeatherAlertColumns.values()) {
            column.setProp(alert, resultSet);
        }
        return alert;
    };
}
