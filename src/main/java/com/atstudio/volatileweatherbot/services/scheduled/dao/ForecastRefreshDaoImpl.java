package com.atstudio.volatileweatherbot.services.scheduled.dao;

import com.atstudio.volatileweatherbot.models.domain.Location;
import com.atstudio.volatileweatherbot.repository.AbstractJdbcRepository;
import com.atstudio.volatileweatherbot.repository.location.LocationColumns;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.atstudio.volatileweatherbot.repository.RepoJdbcUtils.paramSource;
import static com.atstudio.volatileweatherbot.repository.columns.EntityColumns.colName;
import static com.atstudio.volatileweatherbot.repository.columns.EntityColumnsUtils.joinColumnNames;
import static com.atstudio.volatileweatherbot.repository.location.LocationColumns.*;
import static com.atstudio.volatileweatherbot.repository.weatheralert.WeatherAlertColumns.*;
import static com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastColumns.*;
import static java.lang.String.format;

@Repository
public class ForecastRefreshDaoImpl extends AbstractJdbcRepository implements ForecastRefreshDao {

    private static final Duration FORECAST_REFRESH_PERIOD = Duration.of(1, ChronoUnit.HOURS);
    private static final Duration BEFORE_ALERT_PERIOD = Duration.of(1, ChronoUnit.HOURS);

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ForecastRefreshDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Location> getLocationsForForecastRefresh() {
        String locationColumns = joinColumnNames(",", "loc.", LocationColumns.values());

        StringBuilder queryBuilder = new StringBuilder()
                .append(format(" SELECT DISTINCT %s \n FROM %s loc \n ", locationColumns, LOCATIONS_TABLE_NAME))
                .append(format(" JOIN %s al ON loc.%s = al.%s \n",
                        WEATHER_ALERTS_TABLE, colName(LOCATION_CODE), colName(ALERT_LOCATION_CODE)))
                .append(format(" LEFT JOIN %s frk ON ( frk.%s = loc.%s \n",
                        WEATHER_FORECAST_TABLE, colName(FORECAST_LOCATION_CODE), colName(LOCATION_CODE)))
                .append(format(" AND frk.%1$s = (SELECT MAX(%1$s) from %3$s WHERE %2$s = loc.%4$s) )",
                        colName(UPDATE_TIME), colName(FORECAST_LOCATION_CODE), WEATHER_FORECAST_TABLE, colName(LOCATION_CODE)))
                .append(format(" WHERE al.%2$s - timezone(loc.%1$s, :now)::time < interval '%3$d' hour \n",
                        colName(TIMEZONE), colName(ALERT_TIME), BEFORE_ALERT_PERIOD.toHours()))
                .append(format(" AND frk.%1$s IS NULL OR :now - frk.%1$s > interval '%2$d' hour \n", colName(UPDATE_TIME), FORECAST_REFRESH_PERIOD.toHours()));

        return jdbcTemplate.query(
                queryBuilder.toString(),
                paramSource(ImmutableMap.of("now", new Timestamp(Instant.now().toEpochMilli()))),
                locationRowMapper
        );
    }

    static RowMapper<Location> locationRowMapper = (resultSet, i) -> {
        Location result = new Location();
        for (LocationColumns column : LocationColumns.values()) {
            column.setProp(result, resultSet);
        }
        return result;
    };
}